package de.electricdynamite.pasty;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.util.Linkify;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class PastyActivity extends SherlockActivity {
  
    
    public String versionName;
    public int versionCode;
    
	private List<ClipboardItem> ItemList = new ArrayList<ClipboardItem>();
	private ClipboardItemListAdapter ClipboardListAdapter;
	private PastyClient client;
	private PastyPreferencesProvider prefs;
    
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get our own version name and code
		try {
			this.versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			this.versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		// Request features
    	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

    	// Let's draw our layout
	    setContentView(R.layout.main);
	    
	}
    
    public void onResume() {
    	super.onResume();
    	// Let's get preferences
		reloadPreferences();
		
		// Check for network connectivity
		ConnectivityManager connMgr = (ConnectivityManager) 
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
	    	// Create a PastyClient
	    	client = new PastyClient(prefs.getRESTBaseURL(), true);
	    	client.setUsername(prefs.getUsername());
	    	client.setPassword(prefs.getPassword());
			if(!prefs.getUsername().isEmpty() && !prefs.getPassword().isEmpty()) {
		    	// check for the Intent extras
				Log.d(PastyActivity.class.getName(),"Intent Action is "+getIntent().getAction()); 
				Log.d(PastyActivity.class.getName(),"Intent.ACTION_SEND is "+Intent.ACTION_SEND); 
				if (Intent.ACTION_SEND.equals(getIntent().getAction())) {
					Log.d(PastyActivity.class.getName(),"Intent.ACTION_SEND is active!!"); 
			    	Bundle extras = getIntent().getExtras();
			    	if (extras != null) {
			    		String newItem = extras.getString(Intent.EXTRA_TEXT);
			    		addItem(newItem);
			    	}
			    } else {
			    	refreshClipboard();
			    }
			} else {
				showDialog(PastySharedStatics.DIALOG_CREDENTIALS_NOT_SET);
			}
		 } else {
				showDialog(PastySharedStatics.DIALOG_NO_NETWORK);
		 }
    }
    
    public void onStop() {
    	super.onStop();
    	// Let's clean up a little
    	// Nope.avi
    }
    
    public void reloadPreferences() {
    	// Restore preferences
    	 this.prefs = new PastyPreferencesProvider(getBaseContext());
    	
    }
    
   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.menu_add:
        	showDialog(PastySharedStatics.DIALOG_ADD_ID);
	        return true;
        case R.id.menu_settings:
        	Intent settingsActivity = new Intent(getBaseContext(),
                    PastyPreferencesActivity.class);
        	settingsActivity.putExtra("versionName", this.versionName);
        	settingsActivity.putExtra("versionCode", this.versionCode);
        	startActivity(settingsActivity);
            return true;
        case R.id.menu_about:
        	Intent aboutActivity = new Intent(getBaseContext(),
        			PastyAboutActivity.class);
        	startActivity(aboutActivity);
	        return true;
        default:
                return super.onOptionsItemSelected(item);
        }
    }
    
	private void refreshClipboard() {
		if(PastyActivity.this.ClipboardListAdapter != null) {
			PastyActivity.this.ClipboardListAdapter.removeAll();
			PastyActivity.this.ClipboardListAdapter.notifyDataSetChanged();
		}
    	TextView mHelpTextSmall = (TextView) findViewById(R.id.tvHelpTextSmall);
    	mHelpTextSmall.setText("");
    	mHelpTextSmall = null;
		getItemList();
	}
    
    protected AlertDialog onCreateDialog(int id) {
    	// TODO: Start using Fragments, and hence, start using DialogFragments
    	AlertDialog.Builder	builder = null;
		AlertDialog alert = null;
        switch(id) {
        case PastySharedStatics.DIALOG_CONNECTION_ERROR_ID:   	
        	builder = new AlertDialog.Builder(this);
        	builder.setMessage(getString(R.string.error_io))
    			.setTitle(R.string.error_io_title)
        		.setCancelable(false)
        		.setPositiveButton(getString(R.string.button_exit), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        				PastyActivity.this.finish();
        			}
        		});
    			alert = builder.create();
    			alert.show();
				break;
        case PastySharedStatics.DIALOG_NO_NETWORK:   	
        	builder = new AlertDialog.Builder(this);
        	builder.setMessage(getString(R.string.error_no_network))
        		.setCancelable(false)
        		.setTitle(R.string.error_no_network_title)
        		.setPositiveButton(getString(R.string.button_exit), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        				PastyActivity.this.finish();
        			}
        		});
			    /*.setNegativeButton("No", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int id) {
			             dialog.cancel();
			        }
			    });*/
    			alert = builder.create();
    			alert.show();
				break;
        case PastySharedStatics.DIALOG_BAD_ANSWER:
        	builder = new AlertDialog.Builder(this);
        	builder.setMessage(getString(R.string.error_badanswer))
        		.setCancelable(false)
        		.setTitle(R.string.error_badanswer_title)
        		.setPositiveButton(getString(R.string.button_exit), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        				PastyActivity.this.finish();
        			}
        		});
    			alert = builder.create();
    			alert.show();
				break;
        case PastySharedStatics.DIALOG_AUTH_ERROR_ID: 
        	builder = new AlertDialog.Builder(this);  	
        	builder.setMessage(getString(R.string.error_login_failed))
        		.setCancelable(false)
        		.setTitle(R.string.error_login_failed_title)
        		.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        	        	Intent settingsActivity = new Intent(getBaseContext(),
        	                    PastyPreferencesActivity.class);
        	        	settingsActivity.putExtra("versionName", PastyActivity.this.versionName);
        	        	settingsActivity.putExtra("versionCode", PastyActivity.this.versionCode);
        	        	startActivity(settingsActivity);
        			}
        		});
				alert = builder.create();
				alert.show();
				break;
        case PastySharedStatics.DIALOG_CREDENTIALS_NOT_SET: 
        	builder = new AlertDialog.Builder(this);  	
        	builder.setMessage(getString(R.string.error_credentials_not_set))
        		.setCancelable(true)
        		.setTitle(R.string.error_credentials_not_set_title)
        		.setPositiveButton(R.string.button_get_started, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        	        	Intent settingsActivity = new Intent(getBaseContext(),
        	                    PastyPreferencesActivity.class);
        	        	settingsActivity.putExtra("versionName", PastyActivity.this.versionName);
        	        	settingsActivity.putExtra("versionCode", PastyActivity.this.versionCode);
        	        	startActivity(settingsActivity);
        			}
        		});
				alert = builder.create();
				alert.show();
				break;
        case PastySharedStatics.DIALOG_UNKNOWN_ERROR_ID: 
        	builder = new AlertDialog.Builder(this);  	
        	builder.setMessage(getString(R.string.error_unknown))
        		.setCancelable(false)
        		.setTitle(R.string.error_unknown_title)
        		.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        			}
        		});
				alert = builder.create();
				alert.show();
				break;
        case PastySharedStatics.DIALOG_NOT_SUPPORTED_ID: 
        	builder = new AlertDialog.Builder(this);  	
        	builder.setMessage(getString(R.string.error_not_supported))
        		.setCancelable(false)
        		.setPositiveButton(getString(R.string.button_noes), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        			}
        		});
				alert = builder.create();
				alert.show();
				break;
        case PastySharedStatics.DIALOG_ADD_ID:
	        final Dialog addItemDialog = new Dialog(PastyActivity.this);
	
	        addItemDialog.setContentView(R.layout.add_item);
	        addItemDialog.setTitle(getString(R.string.dialog_item_add_title));

			// set click event listener for button
	        addItemDialog.show();
			Button PastyButton		= (Button) addItemDialog.findViewById(R.id.PastyButton);
			PastyButton.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							LinearLayout mDialogLayout = (LinearLayout) v.getParent();
							EditText mNewItemET = (EditText) mDialogLayout.findViewById(R.id.NewItem);
							String mItem = mNewItemET.getText().toString();
							addItemDialog.dismiss();
							addItem(mItem);
						}
					}
			);
			
			if(prefs.getPasteCurrClip()) {
				ClipboardManager clipboard = (ClipboardManager) getSystemService("clipboard");
				if(clipboard.hasText()) {
					EditText mNewItemET = (EditText) addItemDialog.findViewById(R.id.NewItem);
					mNewItemET.setText(clipboard.getText());
					clipboard = null;
				}
			}
			
			break;
        case PastySharedStatics.DIALOG_ABOUT_ID: 
		        Dialog aboutDialog = new Dialog(this);
		
		        aboutDialog.setContentView(R.layout.about_dialog);
		        aboutDialog.setTitle(getString(R.string.about_title));
		        
		        TextView mVersionText = (TextView) aboutDialog.findViewById(R.id.about_version);
		        ImageView aboutImage = (ImageView) aboutDialog.findViewById(R.id.about_image);
		        aboutImage.setImageResource(R.drawable.ic_launcher);
		        mVersionText.setText(getString(R.string.app_name) + " Version " + this.versionName + " ("+Integer.toString(this.versionCode)+")");
		        aboutDialog.show();
		        mVersionText = null;
		        aboutImage = null;
				break;
        default:
            alert = null;
        }
        return alert;
    }
    
    private Handler messageHandler = new Handler() {

        public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        switch (msg.what) {
	        case 1:
	        	listItems(msg.getData());
	        	break;
	        case 2:
	        	confirmAddItem(msg.getData());
	        	break;
	        case 3:
	        	setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
				try {
					confirmDeleteItem();
				} catch (JSONException e) {
					e.printStackTrace();
				}
	        	break;
	        } 
        }
    };

	private void confirmAddItem(Bundle BundleItemId) {
		String ItemId = new String(BundleItemId.getString("ItemId"));
		Log.d(PastyActivity.class.getName(),"ItemId is "+ItemId); 
		
		// item was added
		int duration = Toast.LENGTH_LONG;
		Context context = getApplicationContext();
		CharSequence text = getString(R.string.item_added);
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		toast = null;
		context = null;
		setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
		PastyActivity.this.finish();
	}

	private void confirmDeleteItem() throws JSONException {
		// item was deleted
		int duration = Toast.LENGTH_LONG;
		Context context = getApplicationContext();
		CharSequence text = getString(R.string.item_deleted);
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		toast = null;
		context = null;
		setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
	}

    private void listItems(Bundle ClipboardBundle) {
		JSONArray Clipboard = null;
		if(ClipboardBundle.containsKey("Exception")) {
			short ExceptionId = ClipboardBundle.getShort("Exception");
			switch(ExceptionId) {
				case PastyException.ERROR_AUTHORIZATION_FAILED:
					showDialog(PastySharedStatics.DIALOG_AUTH_ERROR_ID);
					return;
				case PastyException.ERROR_IO_EXCEPTION:
					showDialog(PastySharedStatics.DIALOG_CONNECTION_ERROR_ID);
					return;
				case PastyException.ERROR_ILLEGAL_RESPONSE:
					showDialog(PastySharedStatics.DIALOG_BAD_ANSWER);
					return;
				case PastyException.ERROR_UNKNOWN:
					showDialog(PastySharedStatics.DIALOG_UNKNOWN_ERROR_ID);
					setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
					ProgressBar pbLoading			= (ProgressBar) findViewById(R.id.progressbar_downloading);
					pbLoading.setVisibility(View.GONE);
					pbLoading = null;
					TextView mHelpTextBig = (TextView) findViewById(R.id.tvHelpTextBig);
					mHelpTextBig.setText(R.string.helptext_PastyActivity_error_occured);
					mHelpTextBig = null;
					return;
				default:
					break;
			}
		}
		try {
			Clipboard = new JSONArray(ClipboardBundle.getString("Clipboard"));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ProgressBar pbLoading			= (ProgressBar) findViewById(R.id.progressbar_downloading);
		pbLoading.setVisibility(View.GONE);
		pbLoading = null;

		try {
		    if(Clipboard.length() == 0) {
		       //Clipboard is empty
	        	TextView mHelpTextBig = (TextView) findViewById(R.id.tvHelpTextBig);
	        	mHelpTextBig.setText(R.string.helptext_PastyActivity_clipboard_empty);
	        	mHelpTextBig = null;
	        	TextView mHelpTextSmall = (TextView) findViewById(R.id.tvHelpTextSmall);
	        	mHelpTextSmall.setText(R.string.helptext_PastyActivity_how_to_add);
	        	mHelpTextSmall = null;
	        } else {
				if(Clipboard.length() > 15) {
					throw new Exception();
				}
				for (int i = 0; i < Clipboard.length(); i++) {
					JSONObject Item = Clipboard.getJSONObject(i);
					ClipboardItem cbItem = new ClipboardItem(Item.getString("_id"), Item.getString("item"));
					this.ItemList.add(cbItem);
				}
			
				TextView mHelpTextBig = (TextView) findViewById(R.id.tvHelpTextBig);
				mHelpTextBig.setText(R.string.helptext_PastyActivity_copy);
				mHelpTextBig = null;
			
				ClipboardItemListAdapter adapter = new ClipboardItemListAdapter(this.ItemList, this);
				//Assign adapter to ListView
				ListView listView = (ListView) findViewById(R.id.listItems);
				listView.setAdapter(adapter);
				this.ClipboardListAdapter = adapter;
					
				listView.setOnItemClickListener(new OnItemClickListener() { 
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				    	ClipboardItem Item = PastyActivity.this.ItemList.get(position);
						ClipboardManager sysClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
						Item.copyToClipboard(sysClipboard);
				    	Context context = getApplicationContext();
				    	CharSequence text = getString(R.string.item_copied);
				    	int duration = Toast.LENGTH_LONG;
				    	Toast toast = Toast.makeText(context, text, duration);
				    	toast.show();
				    	toast = null;
				    	context = null;
				    	sysClipboard = null;
				    	text = null;
					    	PastyActivity.this.finish();
					}
				});
				registerForContextMenu(listView);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
    }
    
    private void getItemList() {

    	// Let's look busy
		setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
		

		TextView mHelpTextBig			= (TextView) findViewById(R.id.tvHelpTextBig);
		ProgressBar pbLoading			= (ProgressBar) findViewById(R.id.progressbar_downloading);
		mHelpTextBig.setText(R.string.helptext_PastyActivity_loading);
		pbLoading.setVisibility(View.VISIBLE);
		mHelpTextBig = null;
		pbLoading = null;
		
		new Thread() {
		    public void run() {
				Message msg 			= Message.obtain();
				msg.what				= 1;
				Bundle ClipboardBundle = new Bundle ();
				
				JSONArray Clipboard = null;
				try {
					Clipboard = client.getClipboard();

					ClipboardBundle.putString("Clipboard", Clipboard.toString());
				} catch (PastyException e) {
					ClipboardBundle.putShort("Exception", e.errorId);
					e.printStackTrace();
				} finally {
					msg.setData(ClipboardBundle);
			    	messageHandler.sendMessage(msg);
			    	msg			= null;
				}
		    }		    
		}.start();
    }
    
    private void addItem(final String item) {
    	//EditText NewItem 		= (EditText)findViewById(R.id.NewItem);
    	//String item				= NewItem.getText().toString();
    	if(item != null && item.length() == 0) {
		   	CharSequence text = getString(R.string.empty_item);
		   	Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
		   	toast.show();
    		return;
    	}
    	setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
		new Thread() {
		    public void run() {
				Message msg 			= Message.obtain();
				msg.what				= 2;
				
				String ItemId = "";

				try {
					Log.d(PastyClient.class.toString(),"ITEM is "+item);
					ItemId = client.addItem(item);
				} catch (PastyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Bundle ItemIdBundle = new Bundle ();
				ItemIdBundle.putString("ItemId", ItemId);
				
				msg.setData(ItemIdBundle);
		    	messageHandler.sendMessage(msg);
		    	msg			= null;
		    }
		    
		}.start();
    }
    

    private void deleteItem(final ClipboardItem item, final int position) {
    	setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
		new Thread() {
		    public void run() {
				Message msg 			= Message.obtain();
				msg.what				= 3;

				try {
					Log.d(PastyClient.class.toString(),"ITEM is "+item.getText());
					client.deleteItem(item);
				} catch (PastyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				messageHandler.sendMessage(msg);
		    	msg			= null;
		    }
		    
		}.start();

		PastyActivity.this.ClipboardListAdapter.remove(position);
		PastyActivity.this.ClipboardListAdapter.notifyDataSetChanged();
    }
    
    public class ClipboardItemListAdapter extends BaseAdapter {
    	 
        private List<ClipboardItem> itemList;
     
        private Context context;
     
        public ClipboardItemListAdapter(List<ClipboardItem> itemList, Context context) {
            this.itemList = itemList;
            this.context = context;
        }
     
        public int getCount() {
            return itemList.size();
        }
     
        public ClipboardItem getItem(int position) {
            return itemList.get(position);
        }
        
        public long getItemId(int position) {
            return position;
        }
        
        public String getClipboardItemId(int position) {
        	return itemList.get(position).getId();
        }
        
        public void remove(int position) {
        	this.itemList.remove(position);
        }
        
        public void removeAll() {
        	this.itemList.clear();
        }

		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout itemLayout;
            ClipboardItem Item = itemList.get(position);
     
            itemLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.listitem, parent, false);
     
            TextView tvListItem = (TextView) itemLayout.findViewById(R.id.myListitem);
            tvListItem.setText(Item.getText());
            Linkify.addLinks(tvListItem, Linkify.ALL);
     
     
            return itemLayout;
        }
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
        ContextMenuInfo menuInfo) {
      if (v.getId()==R.id.listItems) {
        menu.setHeaderTitle(getString(R.string.itemContextMenuTitle));
        String[] menuItems = getResources().getStringArray(R.array.itemContextMenu);
        for (int i = 0; i<menuItems.length; i++) {
          menu.add(Menu.NONE, i, i, menuItems[i]);
        }
      }      
    }
    
    public boolean onContextItemSelected(android.view.MenuItem item) {
      AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
      int menuItemIndex = item.getItemId();
      ClipboardItem Item = this.ItemList.get(info.position);
      switch (menuItemIndex) {
      	case PastySharedStatics.ITEM_CONTEXTMENU_COPY_ID:
      		// Copy without exit selected
      		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			Item.copyToClipboard(clipboard);
	    	Context context = getApplicationContext();
	    	CharSequence text = getString(R.string.item_copied);
	    	int duration = Toast.LENGTH_LONG;
	    	Toast toast = Toast.makeText(context, text, duration);
	    	toast.show();
	    	toast = null;
	    	context = null;
	    	clipboard = null;
	    	text = null;
      		break;
      	case PastySharedStatics.ITEM_CONTEXTMENU_SHARE_ID:
      		// Share to another app
      		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
      		shareIntent.setType("text/plain");
      		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, Item.getText());
      		startActivity(Intent.createChooser(shareIntent, getString(R.string.app_share_from_pasty)));
      		break;
      	case PastySharedStatics.ITEM_CONTEXTMENU_DELETE_ID:
      		// Delete selected
      		this.deleteItem(Item, info.position);
      		break;
      }
      //TextView text = (TextView)findViewById(R.id.footer);
      //text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));
      return true;
    }
}