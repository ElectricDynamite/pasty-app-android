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
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

public class PastyClient {
	
	private String 						REST_SERVER_BASE_URL;
	private Boolean						REST_SERVER_TLS_ENABLE;

    private static final String		REST_URI_ITEM			= "/v2/clipboard/item/";
    private static final String 	REST_URI_CLIPBOARD		= "/v2/clipboard/list.json";
	
	private static final int		API_VERSION				= 2;
	
	private String username;
	private String password;
	

	public PastyClient(String restServerBaseURL, Boolean tls) {
		this.REST_SERVER_BASE_URL = restServerBaseURL;
		this.REST_SERVER_TLS_ENABLE = tls;
	}
	
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean deleteItem(ClipboardItem Item) {
		return true;
	}
	
	public void addItem(final String Item) {
		new Thread() {
		    public void run() {
				String url 				= REST_SERVER_BASE_URL+REST_URI_ITEM;
				StringBuilder builder	= new StringBuilder();
				HttpClient client 		= new DefaultHttpClient();
				Message msg 			= Message.obtain();
				msg.what				= 2;
				HttpPost httpPost		= new HttpPost(url);
				JSONObject params		= new JSONObject();
				String basicAuthInfo	= username+":"+password; 
		    	try {
		    		httpPost.setHeader("Authorization", "Basic " + Base64.encodeToString(basicAuthInfo.getBytes(), Base64.NO_WRAP));
		    		params.put("item", Item);
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
		    	//messageHandler.sendMessage(msg);
		    	builder 	= null;
		    	client 		= null;
		    	httpPost	= null;
		    	params		= null;
		    	msg			= null;
		    }
		    
		}.start();
		
	}
	
	
}
