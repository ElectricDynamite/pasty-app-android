package de.electricdynamite.pasty;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
 
public class PastyPreferencesActivity extends PreferenceActivity {
		private static final String URL_ACCOUNT_CREATE	= "https://pastyapp.org/user/create/";
		private static final String URL_TOS				= "https://pastyapp.org/tos/";
		private static final String URL_PRIVACY			= "http://electricdynamite.de/privacy.html"; 

	
	
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.prefs);
                
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
}