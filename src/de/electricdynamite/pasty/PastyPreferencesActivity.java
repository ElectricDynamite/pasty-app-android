package de.electricdynamite.pasty;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
 
public class PastyPreferencesActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {
		private static final String URL_ACCOUNT_CREATE	= "https://pasty.cc/user/create/";
		private static final String URL_TOS				= "https://pasty.cc/tos/";
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
                ActionBar ab = getSherlock().getActionBar();
                ab.setHomeButtonEnabled(true);
                ab.setDisplayHomeAsUpEnabled(true);


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
            	if(mKeyVal.isEmpty()) {
            		mSumVal = getString(R.string.pref_username_defsum);
            	} 
            	prefUsername.setSummary(mSumVal);
            }
            else if (key.equals(KEY_PREF_PASSWORD)) {
            	mKeyVal = sharedPreferences.getString(KEY_PREF_PASSWORD, "");
            	mSumVal = getString(R.string.pref_password_defsum);
            	if(!mKeyVal.isEmpty()) {
            		mSumVal = getString(R.string.pref_password_sum_isset);
            	} 
            	prefPassword.setSummary(mSumVal);
            }
            mKeyVal = null;
            mSumVal = null;
        }
        

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        	switch (item.getItemId()) {
    			case android.R.id.home:
    				Intent intent = new Intent(this,
            			PastyActivity.class);
    				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    				startActivity(intent);
    				return true;
        		default:
        			return super.onOptionsItemSelected(item);
        	}
        } 
}