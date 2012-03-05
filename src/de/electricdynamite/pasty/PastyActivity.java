package de.electricdynamite.pasty;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;
import android.widget.Toast;

public class PastyActivity extends Activity {
  
    static final int DIALOG_CONNECTION_ERROR_ID	= 1;
    static final int DIALOG_AUTH_ERROR_ID		= 2;
    static final int DIALOG_ABOUT_ID			= 10;
    static final int DIALOG_NOT_SUPPORTED_ID	= 127;
    
    static final String PREF_USER				= "pref_username";  
    static final String PREF_PASSWORD			= "pref_password"; 
    static final String PREF_HTTPS				= "pref_usehttps";
    
    static final String URL_HTTP				= "http://mario.blafaselblub.net:8080/";
    static final String URL_HTTPS				= "https://mario.blafaselblub.net:4444/";
    
    private String URL							= "";   
    private String user							= "";
    private String password						= "";

	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// Restore preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
	    setUser(prefs.getString(PREF_USER,""));
	    setPassword(prefs.getString(PREF_PASSWORD,""));
	    setURL(prefs.getBoolean(PREF_HTTPS, true));
	    setContentView(R.layout.main);
		setProgressBarIndeterminateVisibility(true);
		Button PastyButton		= (Button) findViewById(R.id.PastyButton);
		TextView tv				= (TextView) findViewById(R.id.textview);
		tv.setFocusableInTouchMode(true);
		tv.requestFocus();
		// set click event listener for button
		PastyButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						addClip();
					}
				}
		);
		
		Log.d(PastyActivity.class.getName(), "Trying to fetch clips for user: "+getUser());
		getClipList();
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
        MenuInflater inflater = getMenuInflater();
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
    
	private void setUser(String user) {
    	PastyActivity.this.user = user;
    }
    
    private void setPassword(String password) {
    	PastyActivity.this.password = password;
    }
    
    private void setURL(Boolean usehttps) {
    	Log.d(PastyActivity.class.getName(),"usehttps is"+usehttps.toString());
    	if(usehttps) {
    		PastyActivity.this.URL = URL_HTTPS;
    	} else {
    		PastyActivity.this.URL = URL_HTTP;
    	}
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
	        	listClips(msg.getData());
	        	break;
	        case 2:
	        	try {
	        		confirmAddClip(msg.getData());
	        	}
	        	catch (JSONException e) {
	        		e.printStackTrace();
	        	}
	        }
        }

		private void confirmAddClip(Bundle data) throws JSONException {
			String JsonAnswer		= data.getString("response");
	        JSONObject jsonAnswerObject = new JSONObject(JsonAnswer);
	        if(jsonAnswerObject.has("success")) {
	        	// answer is valid
				if(jsonAnswerObject.getBoolean("success") == true) {
					// clip was added
					int duration = Toast.LENGTH_SHORT;
					Context context = getApplicationContext();
				   	CharSequence text = getString(R.string.added_clip);
				   	Toast toast = Toast.makeText(context, text, duration);
				   	toast.show();
				   	PastyActivity.this.finish();
				}
				else {
					// clip was not added
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

		}
    };

    private void listClips(Bundle clips) {
		TextView tvLoading				= (TextView) findViewById(R.id.tv_loading);
		ProgressBar pbLoading			= (ProgressBar) findViewById(R.id.progressbar_downloading);
		
		tvLoading.setVisibility(View.GONE);
		pbLoading.setVisibility(View.GONE);
		tvLoading = null;
    	String JsonAnswer = clips.getString("response");
    	JSONArray ClipArray = new JSONArray();
		try {
	        /* Find TableLayout defined in main.xml */
	        TableLayout tl = (TableLayout)findViewById(R.id.myTableLayout);
	        JSONObject jsonAnswerObject = new JSONObject(JsonAnswer);
	        if(jsonAnswerObject.has("clips")) {
		        ClipArray = jsonAnswerObject.getJSONArray("clips");
				Log.d(PastyActivity.class.getName(),
						"Received " + ClipArray.length()+" clips.");
				for (int i = 0; i < ClipArray.length(); i++) {
					JSONObject Clip = ClipArray.getJSONObject(i);
					 /* Create a new row to be added. */
		             TableRow tr = new TableRow(this);
		             tr.setLayoutParams(new LayoutParams(
		                            LayoutParams.FILL_PARENT));
		            /* Create a TextView to be the row-content. */
		            TextView tv = new TextView(this);
		            tv.setId(i);
		            tv.setOnClickListener(clickListener);
		            tv.setSingleLine(false);
		            tv.setText(Clip.getString("c"));
		            tv.setTextSize(5,3);
		            tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
		                    LayoutParams.WRAP_CONTENT, 1f));
		            tr.addView(tv);  
			        /* Add row to TableLayout. */
			        tl.addView(tr);
			        /*View spacer = new View(this);
			        spacer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			        spacer.setBackgroundColor(0xFFFF0000);
			        tr.addView(spacer);*/
					setProgressBarIndeterminateVisibility(false);
				}
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
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void getClipList() {

    	final String url				= getURL()+"list";
    	final String user				= getUser();
    	final String password			= getPassword();
		new Thread() {
		    public void run() {
				StringBuilder builder = new StringBuilder();
				HttpClient client = new DefaultHttpClient();
				Message msg = Message.obtain();
				msg.what=1;
				HttpPost httpPost = new HttpPost(url);
				String params = "u="+user+"&p="+password;
		    	try {

		        	httpPost.setEntity(new StringEntity(params, "UTF8"));
		        	httpPost.setHeader("Content-type", "application/json");
		        	System.setProperty("http.keepAlive", "false");
		        	Log.i(PastyActivity.class.toString(), "Trying to connect to PastyServer at " + getURL());
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
					} else {
						Log.i(PastyActivity.class.toString(), "Failed to retrieve answer from PastyServer. Bummer.");
				    	builder.append("{ \"success\": false, \"error\": { \"code\": 001, \"message\": \"Forever Alone.\"} }");
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    	Bundle b = new Bundle();
		    	b.putString("response", builder.toString());
		    	msg.setData(b);
		    	messageHandler.sendMessage(msg);
		    }
		    
		}.start();
    }
    
    private void addClip() {
    	EditText NewClip 		= (EditText)findViewById(R.id.NewClip);
    	String clip				= NewClip.getText().toString();
    	if(clip != null && clip.length() == 0) {
    		Log.d(PastyActivity.class.toString(), "Clip ist empty. Bailing out!");
    		return;
    	}
		new Thread() {
		    public void run() {
		    	EditText NewClip 		= (EditText)findViewById(R.id.NewClip);
		    	String url				= URL+"add";
		    	String user				= getUser();
		    	String password			= getPassword();
		    	String clip				= NewClip.getText().toString();
				StringBuilder builder	= new StringBuilder();
				HttpClient client 		= new DefaultHttpClient();
				Message msg 			= Message.obtain();
				msg.what				= 2;
				HttpPost httpPost		= new HttpPost(url);
				String params			= "u="+user+"&p="+password+"&c="+clip;
		    	try {

		        	httpPost.setEntity(new StringEntity(params, "UTF8"));
		        	httpPost.setHeader("Content-type", "application/json");
		        	System.setProperty("http.keepAlive", "false");
		        	Log.d(PastyActivity.class.toString(), "Trying to send clip to PastyServer at " + url);
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
					} else {
						Log.i(PastyActivity.class.toString(), "Failed to retrieve answer from PastyServer. Bummer.");
				    	builder.append("{ \"success\": false, \"error\": { \"code\": 001, \"message\": \"Forever Alone.\"} }");
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    	Bundle b = new Bundle();
		    	b.putString("response", builder.toString());
		    	msg.setData(b);
		    	messageHandler.sendMessage(msg);
		    }
		    
		}.start();
    }
    
    OnClickListener clickListener=new OnClickListener() {
    	public void onClick(View v) {
	    	int id=v.getId();
	    	TextView tv = (TextView)findViewById(v.getId());
	    	CharSequence Clip = tv.getText();
	    	Log.d(PastyActivity.class.getName(), "Clicked on: " + id + ". This is \"" + Clip + "\"");
	    	ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
	    	clipboard.setText(Clip);
	    	Context context = getApplicationContext();
	    	CharSequence text = getString(R.string.copied_clip);
	    	int duration = Toast.LENGTH_SHORT;
	    	Toast toast = Toast.makeText(context, text, duration);
	    	toast.show();
	    	PastyActivity.this.finish();
    	}
    }; 
}