package de.electricdynamite.pasty;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PastyAboutActivity extends SherlockActivity {

	
/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

        ActionBar ab = getSherlock().getActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
    	// Let's draw our layout
	    setContentView(R.layout.about_activity);
		Intent mIntent = getIntent();
        String mVersionName = mIntent.getStringExtra("versionName");
        int mVersionCode = mIntent.getIntExtra("versionCode",0);
        TextView mVersionText = (TextView) findViewById(R.id.about_version);
        mVersionText.setText(getString(R.string.about_version,mVersionName,mVersionCode));
	}

	public void onClick(View view) {
		this.finish();
	}

	@Override
	public void finish() {
		super.finish();
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