package de.electricdynamite.pasty;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

public class PastyPreferencesProvider {
	private SharedPreferences prefs;	
	private String username;	
	private String password;
	private String restServerHost;
	private int restServerPort;
	private String restServerScheme;
	private Boolean useTLS;
	private Boolean pasteCurrClip;
	
	public PastyPreferencesProvider(Context context) {
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
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
		Log.d(PastyPreferencesProvider.class.toString(),"Port is "+this.restServerPort);
		Log.d(PastyPreferencesProvider.class.toString(),"Scheme is "+this.restServerScheme);
		if(this.restServerPort == -1) {
			if(this.restServerScheme == "https") { // TODO this if clause does not work as expected
				Log.d(PastyPreferencesProvider.class.toString(),"IN https if clause");
				this.restServerPort = 443;
			} else {
				this.restServerPort = 80;
			}
		}
		this.pasteCurrClip = prefs.getBoolean(PastySharedStatics.PREF_PASTE_CLIPBOARD, true);
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
}
