package de.electricdynamite.pasty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
		String value1 = extras.getString("Value1");
		String value2 = extras.getString("Value2");
		if (value1 != null && value2 != null) {
		}
	}

	public void onClick(View view) {
		finish();
	}

	@Override
	public void finish() {
		Intent data = new Intent();
		// Return some hard-coded values
		data.putExtra("returnKey1", "Swinging on a star. ");
		data.putExtra("returnKey2", "You could be better then you are. ");
		setResult(RESULT_OK, data);
		super.finish();
	}
}