package de.electricdynamite.pasty;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

public class PastyClipboardActivity extends SherlockFragmentActivity {
	

    private static final String TAG = PastyActivity.class.toString();
    public String versionName;
    public int versionCode;
	private PastyPreferencesProvider prefs;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	try {
			this.versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			this.versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		// Request features
    	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    	
        setContentView(R.layout.main);

		if (savedInstanceState == null) {
			 // Let's get preferences
			reloadPreferences();
			
	    	// Check for network connectivity
	    	ConnectivityManager connMgr = (ConnectivityManager) 
	    			getSystemService(Context.CONNECTIVITY_SERVICE);
	    	NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    	if (networkInfo != null && networkInfo.isConnected()) {
	    		if(!prefs.getUsername().isEmpty() && !prefs.getPassword().isEmpty()) {
	    			// check for the Intent extras
	    			if (Intent.ACTION_SEND.equals(getIntent().getAction())) {
	    				Bundle extras = getIntent().getExtras();
	    				if (extras != null) {
	    					String newItem = extras.getString(Intent.EXTRA_TEXT);
	    					//addItem(newItem);
	    				 }
	    			} else {
	    				getSupportFragmentManager().beginTransaction()
						.replace(R.id.fragment_placeholder, new ClipboardFragment())
						.commit();
	    			}
	    		} else {
	    			showDialog(PastySharedStatics.DIALOG_CREDENTIALS_NOT_SET);
	    		}
	    	} else {
	    		showDialog(PastySharedStatics.DIALOG_NO_NETWORK);
	    	}
		}
    }
    
    @Override
    public void onResume() {
    	super.onResume();


    }
    
    public void reloadPreferences() {
    	// Restore preferences
    	Log.d(TAG, "reloadPreferences() called; reloading preferences");
    	this.prefs = new PastyPreferencesProvider(getBaseContext());
    }
    
   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.menu_add:
        	showDialog(PastySharedStatics.DIALOG_ADD_ID);
	        return true;
        case R.id.menu_settings:
        	Intent settingsActivity = new Intent(getBaseContext(),
                    PastyPreferencesActivity.class);
        	settingsActivity.putExtra("versionName", this.versionName);
        	settingsActivity.putExtra("versionCode", this.versionCode);
        	startActivity(settingsActivity);
            return true;
        case R.id.menu_about:
        	Intent aboutActivity = new Intent(getBaseContext(),
        			PastyAboutActivity.class);
        	startActivity(aboutActivity);
	        return true;
        default:
                return super.onOptionsItemSelected(item);
        }
    }
    
}