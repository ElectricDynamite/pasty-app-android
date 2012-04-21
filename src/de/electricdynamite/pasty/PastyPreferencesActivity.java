package de.electricdynamite.pasty;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
 
public class PastyPreferencesActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
		private static final String URL_ACCOUNT_CREATE	= "https://pastyapp.org/user/create/";
		private static final String URL_TOS				= "https://pastyapp.org/tos/";
		private static final String URL_PRIVACY			= "http://electricdynamite.de/privacy.html"; 
			
	    public static final String KEY_PREF_USERNAME = "pref_username";
	    public static final String KEY_PREF_PASSWORD = "pref_password";
	    public static final String KEY_PREF_VERSION	 = "pref_version";

	    private EditTextPreference	prefUsername;
	    private EditTextPreference	prefPassword;
	    private Preference			prefVersion;
	
	
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.prefs);
                
                prefUsername = (EditTextPreference)getPreferenceScreen().findPreference(KEY_PREF_USERNAME);
                prefPassword = (EditTextPreference)getPreferenceScreen().findPreference(KEY_PREF_PASSWORD);
                prefVersion  = (Preference)getPreferenceScreen().findPreference(KEY_PREF_VERSION);
                
                Preference mPrefAccountCreate = findPreference("pref_account_create");
                mPrefAccountCreate.setOnPreferenceClickListener(
                	      new OnPreferenceClickListener() {
                	    	    @Override
                	    	    public boolean onPreferenceClick(Preference preference) {
                	    	        Intent intent = new Intent(Intent.ACTION_VIEW);
                	    	        intent.setData(Uri.parse(URL_ACCOUNT_CREATE));
                	    	        startActivity(intent);
                	    	        return true;
                	    	    }
                });
                mPrefAccountCreate = null;
                
                Preference mPrefTos = findPreference("pref_tos");
                mPrefTos.setOnPreferenceClickListener(
                	      new OnPreferenceClickListener() {
                	    	    @Override
                	    	    public boolean onPreferenceClick(Preference preference) {
                	    	        Intent intent = new Intent(Intent.ACTION_VIEW);
                	    	        intent.setData(Uri.parse(URL_TOS));
                	    	        startActivity(intent);
                	    	        return true;
                	    	    }
                });
                mPrefTos = null;
                
                Preference mPrefPrivacy = findPreference("pref_privacy");
                mPrefPrivacy.setOnPreferenceClickListener(
                	      new OnPreferenceClickListener() {
                	    	    @Override
                	    	    public boolean onPreferenceClick(Preference preference) {
                	    	        Intent intent = new Intent(Intent.ACTION_VIEW);
                	    	        intent.setData(Uri.parse(URL_PRIVACY));
                	    	        startActivity(intent);
                	    	        return true;
                	    	    }
                });
                mPrefPrivacy = null;
                
        }
        
        @Override
        protected void onResume() {
            super.onResume();
            

            // Setup the initial values
        	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        	String mKeyVal;
        	String mSumVal;
            mKeyVal = sharedPreferences.getString(KEY_PREF_USERNAME, "");
            mSumVal = mKeyVal;
            if(mKeyVal.isEmpty()) {
            	mSumVal = getString(R.string.pref_username_defsum);
            } 
            prefUsername.setSummary(mSumVal);
            
            mKeyVal = sharedPreferences.getString(KEY_PREF_PASSWORD, "");
            mSumVal = getString(R.string.pref_password_defsum);
            if(!mKeyVal.isEmpty()) {
            	mSumVal = getString(R.string.pref_password_sum_isset);
            } 
            prefPassword.setSummary(mSumVal);
            mKeyVal = null;
            mSumVal = null;
            
            Intent mIntent = getIntent();
            mKeyVal = sharedPreferences.getString(KEY_PREF_VERSION, "");
            mSumVal = mIntent.getStringExtra("versionName");
            prefVersion.setSummary(mSumVal);
            mKeyVal = null;
            mSumVal = null;
            mIntent = null;

            // Set up a listener whenever a key changes            
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }
        
        @Override
        protected void onPause() {
            super.onPause();

            // Unregister the listener whenever a key changes            
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);    
        }

        
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // Let's do something a preference value changes
        	String mKeyVal;
        	String mSumVal;
            if (key.equals(KEY_PREF_USERNAME)) {
            	mKeyVal = sharedPreferences.getString(KEY_PREF_USERNAME, "");
            	mSumVal = mKeyVal;
                Log.d(PastyPreferencesActivity.class.getName(),"Username value is: "+mKeyVal);
            	if(mKeyVal.isEmpty()) {
            		mSumVal = getString(R.string.pref_username_defsum);
            	} 
            	prefUsername.setSummary(mSumVal);
            }
            else if (key.equals(KEY_PREF_PASSWORD)) {
            	mKeyVal = sharedPreferences.getString(KEY_PREF_PASSWORD, "");
            	mSumVal = getString(R.string.pref_password_defsum);
                Log.d(PastyPreferencesActivity.class.getName(),"Password value is: "+mKeyVal);
            	if(!mKeyVal.isEmpty()) {
            		mSumVal = getString(R.string.pref_password_sum_isset);
            	} 
            	prefPassword.setSummary(mSumVal);
            }
            mKeyVal = null;
            mSumVal = null;
        }
}