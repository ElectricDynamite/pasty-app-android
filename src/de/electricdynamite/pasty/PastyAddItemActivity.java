package de.electricdynamite.pasty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class PastyAddItemActivity extends Activity {

	
/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			return;
		}
		Log.d(PastyAddItemActivity.class.getName(),"Intent Data URI: "+extras.getString(Intent.EXTRA_TEXT));
	}

	public void onClick(View view) {
		finish();
	}

	@Override
	public void finish() {
		super.finish();
	}
}