package de.electricdynamite.pasty;

/*
 *  Copyright 2012-2013 Philipp Geschke
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

public class PastyPreferencesProvider implements OnSharedPreferenceChangeListener {
	private static Boolean LOCAL_LOG = false;
	private static final String TAG = PastyPreferencesProvider.class.toString();
	private SharedPreferences prefs;	
	private String username;	
	private String password;
	private String restServerHost;
	private int restServerPort;
	private String restServerScheme;
	private Boolean useTLS;
	private Boolean pasteCurrClip;
	private Boolean clickableLinks;
	private int push;
	private Boolean mWasUpdated = false;
	private Context context;
	
	
	public PastyPreferencesProvider(Context context) {
		if(PastySharedStatics.LOCAL_LOG == true) LOCAL_LOG = true;
		if(LOCAL_LOG) Log.v(TAG, "New PastyPreferencesProvider created");
		this.context = context;
		
		reload();
	}
	
	String getUsername() {
		return this.username;
	}
	
	String getPassword() {
		return this.password;
	}
	
	String getRESTServer() {
		return this.restServerHost;
	}		
	
	String getRESTServerScheme() {
		return this.restServerScheme;
	}
	
	int getRESTServerPort() {
		return this.restServerPort;
	}
	
	String getRESTBaseURL() {
		return this.restServerScheme+"://"+this.restServerHost+":"+this.restServerPort;
	}
	
	Boolean getPasteCurrClip() {
		return this.pasteCurrClip;
	}
	
	public Boolean getClickableLinks() {
		return clickableLinks;
	}
	
	public Boolean wasUpdated() {
		if(this.mWasUpdated) {
			this.mWasUpdated = false;
			return true;
		}
		return false;
	}
	
	public void reload() {
//		Log.d(TAG, "reload(): Reloading SharedPreferences");  
        if(prefs != null) prefs.unregisterOnSharedPreferenceChangeListener(this);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // Set up a listener whenever a key changes            
        prefs.registerOnSharedPreferenceChangeListener(this);
		this.username = prefs.getString(PastySharedStatics.PREF_USER,"");
		this.password = prefs.getString(PastySharedStatics.PREF_PASSWORD,"");
		this.useTLS = prefs.getBoolean(PastySharedStatics.PREF_HTTPS, true);
		String strUri = prefs.getString(PastySharedStatics.PREF_SERVER, null);
		if(strUri == null) {
			if(this.useTLS == true) {
				strUri = PastySharedStatics.DEFAULT_REST_URI_HTTPS;
			} else {
				strUri = PastySharedStatics.DEFAULT_REST_URI_HTTP;
			}
		}
		Uri uri = Uri.parse(strUri);
		this.restServerHost = uri.getHost();
		this.restServerPort = uri.getPort();
		this.restServerScheme = uri.getScheme();
		if(this.restServerPort == -1) {
			if(this.restServerScheme.equals(new String("https"))) {
				this.restServerPort = 443;
			} else {
				this.restServerPort = 80;
			}
		}
		this.pasteCurrClip = prefs.getBoolean(PastySharedStatics.PREF_PASTE_CLIPBOARD, true);
		this.clickableLinks = prefs.getBoolean(PastySharedStatics.PREF_CLICKABLE_LINKS, false);
		String mPushStr =  prefs.getString(PastySharedStatics.PREF_PUSH, "0");
        int mPushInt = 0;
    	try {
    		mPushInt = Integer.parseInt(mPushStr);
    	} catch(NumberFormatException e) {
    		Log.w(TAG, "Found non-parseble string while converting to int. This should not happen. The Beast: "+mPushStr);
    	}
    	this.push = mPushInt;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		this.reload();
		this.mWasUpdated = true;
		
	}	
}
