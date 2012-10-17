package de.electricdynamite.pasty;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.Bundle;

public class PastyClipboardActivity extends SherlockFragmentActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.listItems, new ClipboardFragment())
					.commit();
		}
    }
}