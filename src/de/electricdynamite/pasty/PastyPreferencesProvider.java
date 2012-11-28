package de.electricdynamite.pasty;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.preference.PreferenceManager;

public class PastyPreferencesProvider implements OnSharedPreferenceChangeListener {
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
	private Boolean mWasUpdated = false;
	private Context context;
	
	/*
	 * 
	 */
	
	public PastyPreferencesProvider(Context context) {
//		Log.d(TAG, "New PastyPreferencesProvider created");
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
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		this.reload();
		this.mWasUpdated = true;
		
	}	
}
