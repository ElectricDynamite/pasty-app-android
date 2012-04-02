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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.ActionBar;
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

public class PastyActivity extends SherlockActivity {
  
	// Error Dialog IDs
    static final int DIALOG_CONNECTION_ERROR_ID	= 1;
    static final int DIALOG_AUTH_ERROR_ID		= 2;
    static final int DIALOG_UNKNOWN_ERROR_ID	= 99;   		

    // Other Dialog IDs
    static final int DIALOG_ABOUT_ID			= 101;
    static final int DIALOG_NOT_SUPPORTED_ID	= 127;
    
    // Item Context Menu IDs
    static final int ITEM_CONTEXTMENU_COPY_ID	= 0;
    static final int ITEM_CONTEXTMENU_DELETE_ID	= 1;
    
    static final String PREF_USER				= "pref_username";  
    static final String PREF_PASSWORD			= "pref_password"; 
    static final String PREF_HTTPS				= "pref_usehttps";
    static final String PREF_SERVER				= "pref_server";
    
    static final String PREF_SERVER_DEFAULT		= "pastyapp.org";
    
    static final String PORT_HTTP				= "8080";
    static final String PORT_HTTPS				= "4444";
    
    static final String PASTY_URL_ITEM_ADD		= "clipboard/item/add";
    static final String PASTY_URL_ITEM_DELETE	= "clipboard/item/delete/";
    static final String PASTY_URL_CLIPBOARD_LIST= "clipboard/list";
    
    private String URL							= "";
    private String user							= "";
    private String password						= "";

	private List<ClipboardItem> ItemList = new ArrayList<ClipboardItem>();
	private ClipboardItemListAdapter ClipboardListAdapter;
    
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Request features
    	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    	// Let's get preferences
	    setContentView(R.layout.main);
		// set click event listener for button
		Button PastyButton		= (Button) findViewById(R.id.PastyButton);
		PastyButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						addItem();
					}
				}
		);
		TextView tv				= (TextView) findViewById(R.id.tvPasty);
		tv.setFocusableInTouchMode(true);
		tv.requestFocus();
	}
    
    public void onResume() {
    	super.onResume();
    	Log.i(PastyActivity.class.getName(),"onResume(): Reloading items.");
    	// Let's get preferences
		loadPreferences();
		if(PastyActivity.this.ClipboardListAdapter != null) {
			PastyActivity.this.ClipboardListAdapter.removeAll();
			PastyActivity.this.ClipboardListAdapter.notifyDataSetChanged();
		}
		getItemList();
    }
    
    public void onStop() {
    	super.onStop();
    	// Let's clean up a little
    	
    }
    
    public void loadPreferences() {
    	// Restore preferences
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	setUser(prefs.getString(PREF_USER,""));
    	setPassword(prefs.getString(PREF_PASSWORD,""));
    	setURL(prefs.getString(PREF_SERVER, PREF_SERVER_DEFAULT), prefs.getBoolean(PREF_HTTPS, true));
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
        case R.id.menu_settings:
        	Intent settingsActivity = new Intent(getBaseContext(),
                    PastyPreferencesActivity.class);
        	startActivity(settingsActivity);
            return true;
        case R.id.menu_about:
        	showDialog(DIALOG_ABOUT_ID);
	        return true;
        default:
                return super.onOptionsItemSelected(item);
        }
    }
    
	@SuppressWarnings("unused")
	private void refreshClipboardList() {
		// TODO: everything
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
    	
    	String url = proto + server + ":" + port + "/";
    	Log.d(PastyActivity.class.getName(), "URL set to "+url);
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
        case DIALOG_ABOUT_ID: 
		        Dialog dialog = new Dialog(this);
		
		        dialog.setContentView(R.layout.about_dialog);
		        dialog.setTitle(getString(R.string.about_title));
		
		        //TextView text = (TextView) dialog.findViewById(R.id.text);
		        //text.setText(Html.fromHtml(getString(R.string.about_text)));
		        ImageView image = (ImageView) dialog.findViewById(R.id.image);
		        image.setImageResource(R.drawable.ic_launcher);
		        dialog.show();
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
	        	try {
	        		confirmAddItem(msg.getData());
	        	}
	        	catch (JSONException e) {
	        		e.printStackTrace();
	        	}
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

	private void confirmAddItem(Bundle data) throws JSONException {
		String JsonAnswer		= data.getString("response");
        JSONObject jsonAnswerObject = new JSONObject(JsonAnswer);
        if(jsonAnswerObject.has("success")) {
        	// answer is valid
			if(jsonAnswerObject.getBoolean("success") == true) {
				// item was added
				int duration = Toast.LENGTH_SHORT;
				Context context = getApplicationContext();
			   	CharSequence text = getString(R.string.item_added);
			   	Toast toast = Toast.makeText(context, text, duration);
			   	toast.show();
			   	toast = null;
			   	context = null;
			   	PastyActivity.this.finish();
			}
			else {
				// item was not added
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

    private void listItems(Bundle data) {
		TextView tvLoading				= (TextView) findViewById(R.id.tv_loading);
		ProgressBar pbLoading			= (ProgressBar) findViewById(R.id.progressbar_downloading);
		tvLoading.setVisibility(View.GONE);
		pbLoading.setVisibility(View.GONE);
    	
		String JsonAnswer = data.getString("response");
    	JSONArray ItemArray = new JSONArray();
		try {
	        /* Find TableLayout defined in main.xml */
	        //TableLayout tl = (TableLayout)findViewById(R.id.tableItems);
			ListView listView = (ListView) findViewById(R.id.listItems);
	        JSONObject jsonAnswerObject = new JSONObject(JsonAnswer);
	        if(jsonAnswerObject.has("items")) {
		        ItemArray = jsonAnswerObject.getJSONArray("items");
				Log.d(PastyActivity.class.getName(),
						"Received " + ItemArray.length()+" items.");
				if(ItemArray.length() > 10) {
					throw new Exception();
				}
				for (int i = 0; i < ItemArray.length(); i++) {
					JSONObject Item = ItemArray.getJSONObject(i);
					ClipboardItem cbItem = new ClipboardItem(Item.getString("_id"), Item.getString("i"));
					this.ItemList.add(cbItem);
				}
				
				
				ClipboardItemListAdapter adapter = new ClipboardItemListAdapter(this.ItemList, this);
				
				// Assign adapter to ListView
				listView.setAdapter(adapter);
				this.ClipboardListAdapter = adapter;
				
				listView.setOnItemClickListener(new OnItemClickListener() { 
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				    	ClipboardItem Item = PastyActivity.this.ItemList.get(position);
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
				    	PastyActivity.this.finish();
					}
				});
				registerForContextMenu(listView);
	        }
			else if (jsonAnswerObject.has("error")){
				JSONObject ErrorObject = jsonAnswerObject.getJSONObject("error");
				switch (ErrorObject.getInt("code")) {
				case 001:
					showDialog(DIALOG_CONNECTION_ERROR_ID);
					break;
				case 401:
			    	showDialog(DIALOG_AUTH_ERROR_ID);
			    	break;
			    default:
			    	Log.i(PastyActivity.class.getName()," Unknown error received from PastyServer: ERRCODE " + ErrorObject.getString("code") + ": " + ErrorObject.getString("message"));
			    	showDialog(DIALOG_UNKNOWN_ERROR_ID);
				}
			}
			setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void getItemList() {

    	final String url				= getURL()+PASTY_URL_CLIPBOARD_LIST;
    	final String user				= getUser();
    	final String password			= getPassword();

    	// Let's look busy
		setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
		
		/*
		TextView tvLoading				= (TextView) findViewById(R.id.tv_loading);
		ProgressBar pbLoading			= (ProgressBar) findViewById(R.id.progressbar_downloading);
		tvLoading.setVisibility(View.VISIBLE);
		pbLoading.setVisibility(View.VISIBLE); */
		
		new Thread() {
		    public void run() {
				StringBuilder builder = new StringBuilder();
				HttpClient client = new DefaultHttpClient();
				Message msg = Message.obtain();
				msg.what = 1;
				HttpPost httpPost = new HttpPost(url);
				JSONObject params = new JSONObject();
		    	try {
					params.put("u", user);
					params.put("p", password);
					params.put("wid", true);
		        	httpPost.setEntity(new ByteArrayEntity(
		        		    params.toString().getBytes("UTF8")));
		        	httpPost.setHeader("Content-type", "application/json");
		        	System.setProperty("http.keepAlive", "false");
		        	Log.d(PastyActivity.class.toString(), "Trying to connect to PastyServer at " + url);
		        	HttpResponse response = client.execute(httpPost);
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
						Log.e(PastyActivity.class.toString(), statusLine.toString());
						Log.i(PastyActivity.class.toString(), "Failed to retrieve answer from PastyServer. Bummer.");
				    	builder.append("{ \"success\": false, \"error\": { \"code\": 001, \"message\": \"Forever Alone.\"} }");
					}
			    	response	= null;
			    	statusLine	= null;
				} catch (ClientProtocolException e) {
					Log.e(PastyActivity.class.toString(), "Error while talking to server");
					e.printStackTrace();
			    	builder.append("{ \"success\": false, \"error\": { \"code\": 001, \"message\": \"Forever Alone.\"} }");
				} catch (IOException e) {
					Log.e(PastyActivity.class.toString(), "Error while talking to server");
					e.printStackTrace();
			    	builder.append("{ \"success\": false, \"error\": { \"code\": 001, \"message\": \"Forever Alone.\"} }");
				} catch (JSONException e) {
					Log.e(PastyActivity.class.toString(), "Error while creating JSON Object");	
					e.printStackTrace();
			    	builder.append("{ \"success\": false, \"error\": { \"code\": 001, \"message\": \"Forever Alone.\"} }");
				}
		    	finally {
			    	Bundle b = new Bundle();
			    	b.putString("response", builder.toString());
			    	msg.setData(b);
			    	messageHandler.sendMessage(msg);
			    	builder 	= null;
			    	client 		= null;
			    	httpPost	= null;
			    	params		= null;
			    	msg			= null;
		    	}
		    }
		    
		}.start();
    }
    
    private void addItem() {
    	EditText NewItem 		= (EditText)findViewById(R.id.NewItem);
    	String item				= NewItem.getText().toString();
    	if(item != null && item.length() == 0) {
    		Log.d(PastyActivity.class.toString(), "Item ist empty. Bailing out!");
		   	CharSequence text = getString(R.string.empty_item);
		   	Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
		   	toast.show();
    		return;
    	}
    	setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
		new Thread() {
		    public void run() {
		    	EditText NewItem 		= (EditText)findViewById(R.id.NewItem);
		    	String url				= getURL()+PASTY_URL_ITEM_ADD;
		    	String user				= getUser();
		    	String password			= getPassword();
		    	String item				= NewItem.getText().toString();
				StringBuilder builder	= new StringBuilder();
				HttpClient client 		= new DefaultHttpClient();
				Message msg 			= Message.obtain();
				msg.what				= 2;
				HttpPost httpPost		= new HttpPost(url);
				JSONObject params		= new JSONObject();
		    	try {
		    		params.put("u", user);
		    		params.put("p", password);
		    		params.put("i", item);
		        	httpPost.setEntity(new ByteArrayEntity(
		        		    params.toString().getBytes("UTF8")));  
		        	httpPost.setHeader("Content-type", "application/json");
		        	System.setProperty("http.keepAlive", "false");
		        	Log.d(PastyActivity.class.toString(), "Trying to send "+params+" to PastyServer at " + url);
		        	HttpResponse response = client.execute(httpPost);
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
				} catch (JSONException e) {
					e.printStackTrace();
				}
		    	Bundle b = new Bundle();
		    	b.putString("response", builder.toString());
		    	msg.setData(b);
		    	messageHandler.sendMessage(msg);
		    	builder 	= null;
		    	client 		= null;
		    	httpPost	= null;
		    	params		= null;
		    	msg			= null;
		    }
		    
		}.start();
    }
    

    private void deleteItem(final ClipboardItem item, final int position) {
    	setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
    	Log.d(PastyActivity.this.toString(), "Test");
		new Thread() {
		    public void run() {
		    	Log.d(PastyActivity.this.toString(), "Delete Item Run for:" + item.getText());
		    	String url				= getURL()+PASTY_URL_ITEM_DELETE+item.getId();
		    	String user				= getUser();
		    	String password			= getPassword();
				StringBuilder builder	= new StringBuilder();
				HttpClient client 		= new DefaultHttpClient();
				Message msg 			= Message.obtain();
				msg.what				= 3;
				HttpPost httpPost		= new HttpPost(url);
				JSONObject params		= new JSONObject();
		    	try {
		    		params.put("u", user);
		    		params.put("p", password);
		        	httpPost.setEntity(new ByteArrayEntity(
		        		    params.toString().getBytes("UTF8")));
		        	httpPost.setHeader("Content-type", "application/json");
		        	System.setProperty("http.keepAlive", "false");
		        	HttpResponse response = client.execute(httpPost);
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
				} catch (JSONException e) {
					e.printStackTrace();
				}
		    	Bundle b = new Bundle();
		    	b.putString("response", builder.toString());
		    	msg.setData(b);
		    	messageHandler.sendMessage(msg);
		    	builder 	= null;
		    	client 		= null;
		    	httpPost	= null;
		    	params		= null;
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