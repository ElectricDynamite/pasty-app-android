package de.electricdynamite.pasty;

import android.os.Bundle;
import android.preference.PreferenceActivity;
 
public class PastyPreferencesActivity extends PreferenceActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.prefs);
        }
}