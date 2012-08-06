package de.electricdynamite.pasty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class PastyAddItemActivity extends Activity {
	private PastyClient client;
	private PastyPreferencesProvider prefs;

	
/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			return;
		}

    	// Restore preferences
    	 this.prefs = new PastyPreferencesProvider(getBaseContext());
		
		// Check for network connectivity
		ConnectivityManager connMgr = (ConnectivityManager) 
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
	    	// Create a PastyClient
	    	client = new PastyClient(prefs.getRESTBaseURL(), true);
	    	client.setUsername(prefs.getUsername());
	    	client.setPassword(prefs.getPassword());
			if(!prefs.getUsername().isEmpty() && !prefs.getPassword().isEmpty()) {
				Log.d(PastyAddItemActivity.class.getName(),"Intent Data URI: "+extras.getString(Intent.EXTRA_TEXT));
			   	Toast infoToast = Toast.makeText(getApplicationContext(), getString(R.string.item_adding), Toast.LENGTH_SHORT);
			   	infoToast.show();
			   	infoToast = null;
				try {
					client.addItem(extras.getString(Intent.EXTRA_TEXT));
				   	Toast confirmToast = Toast.makeText(getApplicationContext(), getString(R.string.item_added), Toast.LENGTH_LONG);
				   	confirmToast.show();
				   	confirmToast = null;
				} catch (PastyException e) {
				   	Toast errorToast = Toast.makeText(getApplicationContext(), getString(R.string.error_adding), Toast.LENGTH_LONG);
				   	errorToast.show();
				   	errorToast = null;
					e.printStackTrace();
				}
			} else {
			   	Toast errorToast = Toast.makeText(getApplicationContext(), getString(R.string.error_not_supported), Toast.LENGTH_LONG);
			   	errorToast.show();
			   	errorToast = null;
			}
		 } else {
			   	Toast errorToast = Toast.makeText(getApplicationContext(), getString(R.string.error_no_network_title), Toast.LENGTH_LONG);
			   	errorToast.show();
			   	errorToast = null;
		 }
	}

	public void onClick(View view) {
		this.finish();
	}

	@Override
	public void finish() {
		super.finish();
	}
}