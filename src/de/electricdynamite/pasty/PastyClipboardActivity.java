package de.electricdynamite.pasty;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import de.electricdynamite.pasty.AddItemFragment.AddItemFragmentCallbackListener;
import de.electricdynamite.pasty.ClipboardFragment.PastyClipboardFragmentListener;
import de.electricdynamite.pasty.PastyAlertDialogFragment.PastyAlertDialogListener;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

public class PastyClipboardActivity extends SherlockFragmentActivity implements PastyAlertDialogListener, PastyClipboardFragmentListener, AddItemFragmentCallbackListener {
	

    private static final String TAG = PastyActivity.class.toString();
    public String versionName;
    public int versionCode;
	protected PastyPreferencesProvider prefs;
	private static ClipboardFragment mClipboardFragment = new ClipboardFragment();
	private static AddItemFragment mAddItemFragment = new AddItemFragment();
	
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
    	this.prefs = new PastyPreferencesProvider(getBaseContext());
        setContentView(R.layout.main);

		if (savedInstanceState == null) {
		}
    }
    
    
    @Override
    public void onResume() {
    	super.onResume();
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
    				if(!mClipboardFragment.isAdded()) {
    					Log.d(TAG, "onResume(): Adding ClipboardFragment");
    					getSupportFragmentManager().beginTransaction()
    					.replace(R.id.fragment_placeholder, mClipboardFragment)
    					.commit();
    				}
    			}
    		} else {
    			showAlertDialog(PastySharedStatics.DIALOG_CREDENTIALS_NOT_SET);
    			// TODO make sure this is not "skipped" with back-key
    		}
    	} else {
    		showAlertDialog(PastySharedStatics.DIALOG_NO_NETWORK);
    	}
    }
    

    protected void showAlertDialog(int id) {
        FragmentManager fm = getSupportFragmentManager();
        PastyAlertDialogFragment AlertDialog = new PastyAlertDialogFragment(id);
        AlertDialog.setCancelable(false);
        AlertDialog.show(fm, "fragment_alert_name");
    }
    
    public void reloadPreferences() {
    	// Restore preferences
    	Log.d(TAG, "reloadPreferences() called; reloading preferences");
    	prefs.reload();
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
        	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        	Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        	if (prev != null) {
        		ft.remove(prev);
        	}
        	ft.addToBackStack(null);

	        // Create and show the dialog.
	        mAddItemFragment.show(ft, "dialog");
	        return true;
        case R.id.menu_reload:
        	mClipboardFragment.restartLoading();
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
	
	@Override
	public void onFinishPastyAlertDialog(int signal) {
		actOnSignal(signal);
	}

	private void actOnSignal(int signal) {

		switch(signal) {
			case PastySharedStatics.SIGNAL_EXIT:
				this.finish();
				break;
			case PastySharedStatics.SIGNAL_ACTIVITY_SETTINGS:
				Intent settingsActivity = new Intent(getBaseContext(),
	                    PastyPreferencesActivity.class);
	        	settingsActivity.putExtra("versionName", versionName);
	        	settingsActivity.putExtra("versionCode", versionCode);
	        	startActivity(settingsActivity);
				break;
			case PastySharedStatics.SIGNAL_ACTIVITY_ABOUT:
	        	Intent aboutActivity = new Intent(getBaseContext(),
	        			PastyAboutActivity.class);
	        	startActivity(aboutActivity);
				break;
		}
	}

	@Override
	public void onPastyClipboardFragmentSignal(int signal) {
		actOnSignal(signal);		
	}


	@Override
	public void onPastyClipboardFragmentSignal(int signal, int dialogId) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onAddItemFragmentCallbackSignal(int signal) {
		actOnSignal(signal);			
	}
	
	
	
    
}