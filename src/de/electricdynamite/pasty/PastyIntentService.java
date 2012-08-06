package de.electricdynamite.pasty;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PastyIntentService extends IntentService {

	public PastyIntentService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		Bundle extras = arg0.getExtras();
		if (extras == null) {
			return;
		}
		Log.d(PastyAddItemActivity.class.getName(),"Intent Data URI: "+extras.getString(Intent.EXTRA_TEXT));
	}

}
