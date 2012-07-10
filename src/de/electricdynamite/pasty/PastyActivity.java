package de.electricdynamite.pasty;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
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
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
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
  
	// Error Dialog IDs
    static final int DIALOG_CONNECTION_ERROR_ID	= 1;
    static final int DIALOG_AUTH_ERROR_ID		= 2;
    static final int DIALOG_CREDENTIALS_NOT_SET = 3;
    static final int DIALOG_UNKNOWN_ERROR_ID	= 99;   		

    // Other Dialog IDs
    static final int DIALOG_ABOUT_ID			= 101;
    static final int DIALOG_ADD_ID				= 102;
    static final int DIALOG_NOT_SUPPORTED_ID	= 127;
    
    // Item Context Menu IDs
    static final int ITEM_CONTEXTMENU_COPY_ID	= 0;
    static final int ITEM_CONTEXTMENU_SHARE_ID	= 1;
    static final int ITEM_CONTEXTMENU_DELETE_ID	= 2;
    
    static final String PREF_USER				= "pref_username";  
    static final String PREF_PASSWORD			= "pref_password"; 
    static final String PREF_HTTPS				= "pref_usehttps";
    static final String PREF_SERVER				= "pref_server";
    static final String PREF_PASTE_CLIPBOARD	= "pref_paste_clipboard";
    
    static final String PREF_SERVER_DEFAULT		= "mario.blafaselblub.net";
    
    static final String PORT_HTTP				= "80";
    static final String PORT_HTTPS				= "4444";
    
    static final String PASTY_REST_URI_ITEM		= "/v1/clipboard/item/";
    static final String PASTY_REST_URI_CLIPBOARD= "/v1/clipboard/list.json";
    
    public String versionName;
    public int versionCode;
    
    private String URL							= "";
    private String user							= "";
    private String password						= "";
    private Boolean PREF_CURR_PASTE_CLIPBOARD	= true;

	private List<ClipboardItem> ItemList = new ArrayList<ClipboardItem>();
	private ClipboardItemListAdapter ClipboardListAdapter;
	private PastyClient client;
    
	
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
    	
    	// Let's get preferences
	    setContentView(R.layout.main);
	}
    
    public void onResume() {
    	super.onResume();
    	// Let's get preferences
		loadPreferences();
    	// Create a PastyClient
    	client = new PastyClient(getURL(), true);
    	client.setUsername(getUser());
    	client.setPassword(getPassword());
		if(!getUser().isEmpty() && !getPassword().isEmpty()) {
			refreshClipboard();
		} else {
			showDialog(DIALOG_CREDENTIALS_NOT_SET);
		}
    }
    
    public void onStop() {
    	super.onStop();
    	// Let's clean up a little
    	// Nope.avi
    }
    
    public void loadPreferences() {
    	// Restore preferences
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	setUser(prefs.getString(PREF_USER,""));
    	setPassword(prefs.getString(PREF_PASSWORD,""));
    	setURL(prefs.getString(PREF_SERVER, PREF_SERVER_DEFAULT), prefs.getBoolean(PREF_HTTPS, true));
    	this.PREF_CURR_PASTE_CLIPBOARD = prefs.getBoolean(PREF_PASTE_CLIPBOARD, true);
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
        	showDialog(DIALOG_ADD_ID);
	        return true;
        case R.id.menu_settings:
        	Intent settingsActivity = new Intent(getBaseContext(),
                    PastyPreferencesActivity.class);
        	settingsActivity.putExtra("versionName", this.versionName);
        	settingsActivity.putExtra("versionCode", this.versionCode);
        	startActivity(settingsActivity);
            return true;
        case R.id.menu_about:
        	showDialog(DIALOG_ABOUT_ID);
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
	
	private void setUser(String user) {
    	PastyActivity.this.user = user;
    }
    
    private void setPassword(String password) {
    	PastyActivity.this.password = password;
    }
    
    private void setURL(String server, Boolean usehttps) {    	
    	String proto	= "";
    	String port		= "";
    	
    	if(usehttps) {
    		proto	= "https://";
    		port		= PORT_HTTPS;
    	} else {
    		proto	= "http://";
    		port		= PORT_HTTP;
    	}
    	
    	String url = proto + server + ":" + port;
    	PastyActivity.this.URL = url;
    	proto		= null;
    	port		= null;
    	server		= null;
    	usehttps	= null;
    	url			= null;    		
    }
    
    private String getUser() {
		return PastyActivity.this.user;
    }
    
    private String getPassword() {
		return PastyActivity.this.password;
    }

	private String getURL() {
		return PastyActivity.this.URL;
	}
    
    protected AlertDialog onCreateDialog(int id) {
    	// TODO: Start using Fragments, and hence, start using DialogFragments
    	AlertDialog.Builder	builder = null;
		AlertDialog alert = null;
        switch(id) {
        case DIALOG_CONNECTION_ERROR_ID:   	
        	builder = new AlertDialog.Builder(this);
        	builder.setMessage(getString(R.string.error_badanswer))
        		.setCancelable(false)
        		.setPositiveButton(getString(R.string.button_noes), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        				//PastyActivity.this.finish();
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
        case DIALOG_AUTH_ERROR_ID: 
        	builder = new AlertDialog.Builder(this);  	
        	builder.setMessage(getString(R.string.error_loginfailed))
        		.setCancelable(false)
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
        case DIALOG_CREDENTIALS_NOT_SET: 
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
        case DIALOG_UNKNOWN_ERROR_ID: 
        	builder = new AlertDialog.Builder(this);  	
        	builder.setMessage(getString(R.string.error_unknown))
        		.setCancelable(false)
        		.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        			}
        		});
				alert = builder.create();
				alert.show();
				break;
        case DIALOG_NOT_SUPPORTED_ID: 
        	builder = new AlertDialog.Builder(this);  	
        	builder.setMessage(getString(R.string.error_notsupported))
        		.setCancelable(false)
        		.setPositiveButton(getString(R.string.button_noes), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        			}
        		});
				alert = builder.create();
				alert.show();
				break;
        case DIALOG_ADD_ID:
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
			
			if(this.PREF_CURR_PASTE_CLIPBOARD) {
				ClipboardManager clipboard = (ClipboardManager) getSystemService("clipboard");
				if(clipboard.hasText()) {
					EditText mNewItemET = (EditText) addItemDialog.findViewById(R.id.NewItem);
					mNewItemET.setText(clipboard.getText());
					clipboard = null;
				}
			}
			
			break;
        case DIALOG_ABOUT_ID: 
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
					confirmDeleteItem(msg.getData());
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
		int duration = Toast.LENGTH_SHORT;
		Context context = getApplicationContext();
		CharSequence text = getString(R.string.item_added);
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		toast = null;
		context = null;
		setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
		PastyActivity.this.finish();
	}

	private void confirmDeleteItem(Bundle data) throws JSONException {
		String JsonAnswer		= data.getString("response");
        JSONObject jsonAnswerObject = new JSONObject(JsonAnswer);
        if(jsonAnswerObject.has("success")) {
        	// answer is valid
			if(jsonAnswerObject.getBoolean("success") == true) {
				// item was deleted
				int duration = Toast.LENGTH_SHORT;
				Context context = getApplicationContext();
			   	CharSequence text = getString(R.string.item_deleted);
			   	Toast toast = Toast.makeText(context, text, duration);
			   	toast.show();
			   	toast = null;
			   	context = null;
			}
			else {
				// item was not deleted
				JSONObject jsonError = jsonAnswerObject.getJSONObject("error");
				switch (jsonError.getInt("code")) {
				case 401:
			    	showDialog(DIALOG_AUTH_ERROR_ID);
			    	break;
				default:
				   	Log.i(PastyActivity.class.getName()," Unknown error received from PastyServer: ERRCODE " + jsonError.getString("code") + ": " + jsonError.getString("message"));		
				}
			}
        }
        else {
        	// answer is invalid
        }
		setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
	}

    private void listItems(Bundle ClipboardBundle) {
		JSONArray Clipboard = null;
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
				    	int duration = Toast.LENGTH_SHORT;
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
				
				JSONArray Clipboard = null;
				try {
					Clipboard = client.getClipboard();
				} catch (PastyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Bundle ClipboardBundle = new Bundle ();
				ClipboardBundle.putString("Clipboard", Clipboard.toString());
				
				msg.setData(ClipboardBundle);
		    	messageHandler.sendMessage(msg);
		    	msg			= null;
		    }		    
		}.start();
    }
    
    private void addItem(final String item) {
    	//EditText NewItem 		= (EditText)findViewById(R.id.NewItem);
    	//String item				= NewItem.getText().toString();
    	if(item != null && item.length() == 0) {
		   	CharSequence text = getString(R.string.empty_item);
		   	Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
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
		    	String url				= getURL()+PASTY_REST_URI_ITEM+item.getId();
		    	String user				= getUser();
		    	String password			= getPassword();
				StringBuilder builder	= new StringBuilder();
				HttpClient client 		= new DefaultHttpClient();
				Message msg 			= Message.obtain();
				msg.what				= 3;
				HttpDelete httpDel		= new HttpDelete(url);
		    	try {
		    		httpDel.setHeader("Content-type", "application/json");
		    		httpDel.setHeader("X-Pasty-User", user);  
		    		httpDel.setHeader("X-Pasty-Password", password);
		        	System.setProperty("http.keepAlive", "false");
		        	HttpResponse response = client.execute(httpDel);
		        	StatusLine statusLine = response.getStatusLine();
					int statusCode = statusLine.getStatusCode();
					if (statusCode == 200) {
						HttpEntity entity = response.getEntity();
						InputStream content = entity.getContent();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(content));
						String line;
						while ((line = reader.readLine()) != null) {
							builder.append(line);
						}
				    	entity		= null;
				    	content		= null;
				    	reader		= null;
					} else {
						Log.d(PastyActivity.class.toString(), "Failed to retrieve answer from PastyServer. Bummer.");
				    	builder.append("{ \"success\": false, \"error\": { \"code\": 001, \"message\": \"Forever Alone.\"} }");
					}
			    	response	= null;
			    	statusLine	= null;
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    	Bundle b = new Bundle();
		    	b.putString("response", builder.toString());
		    	msg.setData(b);
		    	messageHandler.sendMessage(msg);
		    	builder 	= null;
		    	client 		= null;
		    	httpDel		= null;
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
      	case ITEM_CONTEXTMENU_COPY_ID:
      		// Copy without exit selected
      		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			Item.copyToClipboard(clipboard);
	    	Context context = getApplicationContext();
	    	CharSequence text = getString(R.string.item_copied);
	    	int duration = Toast.LENGTH_SHORT;
	    	Toast toast = Toast.makeText(context, text, duration);
	    	toast.show();
	    	toast = null;
	    	context = null;
	    	clipboard = null;
	    	text = null;
      		break;
      	case ITEM_CONTEXTMENU_SHARE_ID:
      		// Share to another app
      		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
      		shareIntent.setType("text/plain");
      		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, Item.getText());
      		startActivity(Intent.createChooser(shareIntent, getString(R.string.app_share_from_pasty)));
      		break;
      	case ITEM_CONTEXTMENU_DELETE_ID:
      		// Delete selected
      		this.deleteItem(Item, info.position);
      		break;
      }
      //TextView text = (TextView)findViewById(R.id.footer);
      //text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));
      return true;
    }
}