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


import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.BaseAdapter;
 
public class PastyPreferencesActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {
		private static final String TAG = PastyPreferencesActivity.class.toString();
			
	    public static final String KEY_PREF_USERNAME = "pref_username";
	    public static final String KEY_PREF_PASSWORD = "pref_password";
	    public static final String KEY_PREF_VERSION	 = "pref_version";
	    public static final String KEY_PREF_CLICKABLE_LINKS	 = "pref_clickable_links";
	    public static final String KEY_PREF_PUSH = "pref_push";

	    private Boolean LOCAL_LOG = false;
	    private EditTextPreference	prefUsername;
	    private EditTextPreference	prefPassword;
	    private Preference			prefVersion;
	    private PreferenceScreen	prefPushScreen;
	    private BaseAdapter			prefPushScreenAdapter;
	
	
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                if(PastySharedStatics.LOCAL_LOG == Boolean.TRUE) LOCAL_LOG = true;
                ActionBar ab = getSherlock().getActionBar();
                ab.setHomeButtonEnabled(true);
                ab.setDisplayHomeAsUpEnabled(true);


                addPreferencesFromResource(R.xml.prefs);
                
                prefUsername = (EditTextPreference)getPreferenceScreen().findPreference(KEY_PREF_USERNAME);
                prefPassword = (EditTextPreference)getPreferenceScreen().findPreference(KEY_PREF_PASSWORD);
                prefPushScreen = (PreferenceScreen)getPreferenceScreen().findPreference(PastySharedStatics.PREF_CAT_PUSH);
                prefVersion  = (Preference)getPreferenceScreen().findPreference(KEY_PREF_VERSION);
                prefPushScreenAdapter = (BaseAdapter)getPreferenceScreen().getRootAdapter();
        }
        
		@Override
        protected void onResume() {
            super.onResume();

            // Setup the initial values
        	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
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
            
            Boolean mKeyBool = sharedPreferences.getBoolean(PastySharedStatics.PREF_PUSH_GCM, false);
            mSumVal = getString(R.string.pref_push_disabled_sum);
            if(mKeyBool == Boolean.TRUE) {
            	mSumVal = getString(R.string.pref_push_enabled_sum);
            } 
            prefPushScreen.setSummary(mSumVal);
            mKeyBool = null;
             
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
			if(LOCAL_LOG) Log.v(TAG,"onSharedPreferenceChanged(): setting updated: "+key);
        	String mKeyVal;
        	String mSumVal;
            if (key.equals(KEY_PREF_USERNAME)) {
            	mKeyVal = sharedPreferences.getString(KEY_PREF_USERNAME, "");
            	mSumVal = mKeyVal;
            	if(mKeyVal.isEmpty()) {
            		mSumVal = getString(R.string.pref_username_defsum);
            	} 
            	prefUsername.setSummary(mSumVal);
            } else if (key.equals(KEY_PREF_PASSWORD)) {
            	mKeyVal = sharedPreferences.getString(KEY_PREF_PASSWORD, "");
            	mSumVal = getString(R.string.pref_password_defsum);
            	if(!mKeyVal.isEmpty()) {
            		mSumVal = getString(R.string.pref_password_sum_isset);
            	} 
            	prefPassword.setSummary(mSumVal);
            } else if (key.equals(PastySharedStatics.PREF_PUSH_GCM)) {
            	Boolean mKeyBool = sharedPreferences.getBoolean(PastySharedStatics.PREF_PUSH_GCM, false);
            	mSumVal = getString(R.string.pref_push_disabled_sum);
            	if(mKeyBool == Boolean.TRUE) {
            		mSumVal = getString(R.string.pref_push_enabled_sum);
            	} 
            	prefPushScreen.setSummary(mSumVal);
            	prefPushScreenAdapter.notifyDataSetChanged();
            	mKeyBool = null;
            } 
            mKeyVal = null;
            mSumVal = null;
        }
        

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        	switch (item.getItemId()) {
    			case android.R.id.home:
    				/*
    				 * We will not call Home using an intend, but instead will finish() this activity.
    				 * Intent intent = new Intent(this,
            			PastyActivity.class);
    				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    				startActivity(intent);
    				*/
    				finish();
    				return true;
        		default:
        			return super.onOptionsItemSelected(item);
        	}
        } 
}
