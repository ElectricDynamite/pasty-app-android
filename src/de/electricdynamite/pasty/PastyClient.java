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
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.DefaultedHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
import android.util.Log;

public class PastyClient {
	private static final String		TAG = PastyClient.class.toString();
	private static final String		REST_SERVER_DEFAULT_BASE_HOST = "api.pastyapp.org";
	private static final int		REST_SERVER_DEFAULT_PORT_HTTP = 80;
	private static final int 		REST_SERVER_DEFAULT_PORT_HTTPS = 443;
	private static final boolean	REST_SERVER_DEFAULT_TLS_ENABLED = true;
	
	private String 						REST_SERVER_BASE_URL;
	private Boolean						REST_SERVER_TLS_ENABLE;

    private static final String		REST_URI_ITEM			= "/v2/clipboard/item/";
    private static final String 	REST_URI_CLIPBOARD		= "/v2/clipboard/list.json";
	
	public static final int			API_VERSION				= 2;
	public static final String		VERSION = "0.1.0";
	
	private String username;
	private String password;
	private final String httpUserAgent = "PastyClient for Android/"+PastyClient.VERSION;
	private DefaultedHttpParams defaultHttpParams;
	

	public PastyClient(String restServerBaseURL, Boolean tls) {
		this.REST_SERVER_BASE_URL = restServerBaseURL;
		this.REST_SERVER_TLS_ENABLE = tls;
		initializeEnvironment();
	}
	
	public PastyClient() {
		String url = "";
		if(PastyClient.REST_SERVER_DEFAULT_TLS_ENABLED) {
			url = "https://";
		} else {
			url = "http://";
		}
		url = url+PastyClient.REST_SERVER_DEFAULT_BASE_HOST;
		this.REST_SERVER_BASE_URL = url;
		this.REST_SERVER_TLS_ENABLE = PastyClient.REST_SERVER_DEFAULT_TLS_ENABLED;
		initializeEnvironment();
	}
	
	private void initializeEnvironment() {
		//this.defaultHttpParams.setParameter(CoreProtocolPNames.USER_AGENT, this.httpUserAgent);
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public JSONArray getClipboard() throws PastyException {
		String url 				= REST_SERVER_BASE_URL+REST_URI_CLIPBOARD;
//		Log.d(TAG,"REST URL is "+url);
		StringBuilder builder	= new StringBuilder();
		HttpClient client 		= new DefaultHttpClient();
		HttpGet httpGet			= new HttpGet(url);
		String basicAuthInfo	= username+":"+password; 
		
		try {
			httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString(basicAuthInfo.getBytes(), Base64.NO_WRAP));
		    httpGet.setHeader("Content-type", "application/json"); 
		    System.setProperty("http.keepAlive", "false");
//		    Log.d(TAG, "Starting REST CALL");
		    HttpResponse response = client.execute(httpGet);
//		    Log.d(TAG, "REST CALL finished");
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
				JSONObject jsonResponse = new JSONObject(builder.toString());
				JSONObject jsonPayload = jsonResponse.getJSONObject("payload");
				JSONArray jsonClipboard = jsonPayload.getJSONArray("items");
				builder 	= null;
				client 		= null;
				httpGet		= null;
				response	= null;
				statusLine	= null;
				return jsonClipboard;
			} else if(statusCode == 401) {
				throw new PastyException(PastyException.ERROR_AUTHORIZATION_FAILED);
			} else {
				throw new PastyException(PastyException.ERROR_ILLEGAL_RESPONSE);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new PastyException(PastyException.ERROR_IO_EXCEPTION);
		} catch (IOException e) {
			e.printStackTrace();
			throw new PastyException(PastyException.ERROR_IO_EXCEPTION);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new PastyException(PastyException.ERROR_ILLEGAL_RESPONSE);
		}
	}
	
	public String addItem(final String Item) throws PastyException {
		String url 				= REST_SERVER_BASE_URL+REST_URI_ITEM;
		Log.d(PastyClient.class.toString(),"url is "+url);
		StringBuilder builder	= new StringBuilder();
		HttpClient client 		= new DefaultHttpClient();
		HttpPost httpPost		= new HttpPost(url);
		JSONObject params		= new JSONObject();
		String basicAuthInfo	= username+":"+password; 
		try {
			httpPost.setHeader("Authorization", "Basic " + Base64.encodeToString(basicAuthInfo.getBytes(), Base64.NO_WRAP));
		    params.put("item", Item);
		    httpPost.setEntity(new ByteArrayEntity(
		        params.toString().getBytes("UTF8")));
		    httpPost.setHeader("Content-type", "application/json");
		    httpPost.setHeader("User-Agent", "PastyClient for Android/"+PastyClient.VERSION);
		    System.setProperty("http.keepAlive", "false");
		    HttpResponse response = client.execute(httpPost);
		    StatusLine statusLine = response.getStatusLine();
		    int statusCode = statusLine.getStatusCode();
		    if (statusCode == 201) {
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
				JSONObject jsonResponse = new JSONObject(builder.toString());
				JSONObject jsonPayload = jsonResponse.getJSONObject("payload");
				String ItemId = jsonPayload.getString("_id");
				builder 	= null;
				client 		= null;
				httpPost	= null;
				params		= null;
				response	= null;
				statusLine	= null;
				return ItemId;
			} else if(statusCode == 401) {
				throw new PastyException(PastyException.ERROR_AUTHORIZATION_FAILED);
			} else {
				throw new PastyException(PastyException.ERROR_ILLEGAL_RESPONSE);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new PastyException(PastyException.ERROR_IO_EXCEPTION);
		} catch (IOException e) {
			e.printStackTrace();
			throw new PastyException(PastyException.ERROR_IO_EXCEPTION);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new PastyException(PastyException.ERROR_ILLEGAL_RESPONSE);
		}
	}	
	
	public void deleteItem(ClipboardItem Item) throws PastyException {
		String url 				= REST_SERVER_BASE_URL+REST_URI_ITEM+Item.getId();
		Log.d(PastyClient.class.toString(),"url is "+url);
		HttpClient client 		= new DefaultHttpClient();
		HttpDelete httpDelete	= new HttpDelete(url);
		String basicAuthInfo	= username+":"+password; 
		try {
			httpDelete.setHeader("Authorization", "Basic " + Base64.encodeToString(basicAuthInfo.getBytes(), Base64.NO_WRAP));
		    httpDelete.setHeader("Content-type", "application/json");
		    httpDelete.setHeader("User-Agent", "PastyClient for Android/"+PastyClient.VERSION);
		    System.setProperty("http.keepAlive", "false");
		    HttpResponse response = client.execute(httpDelete);
		    StatusLine statusLine = response.getStatusLine();
		    int statusCode = statusLine.getStatusCode();
		    if(statusCode == 200) {
				client 		= null;
				httpDelete	= null;
				response	= null;
				statusLine	= null;
			} else if(statusCode == 401) {
				throw new PastyException(PastyException.ERROR_AUTHORIZATION_FAILED);
			} else {
				throw new PastyException(PastyException.ERROR_ILLEGAL_RESPONSE);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new PastyException(PastyException.ERROR_IO_EXCEPTION);
		} catch (IOException e) {
			e.printStackTrace();
			throw new PastyException(PastyException.ERROR_IO_EXCEPTION);
		}
	}	
	
}
