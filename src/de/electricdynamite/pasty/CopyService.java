package de.electricdynamite.pasty;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.util.Log;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class CopyService extends IntentService {
	public static final String TAG = CopyService.class.toString();
	static final String name = "CopyService";
	Handler mHandler;
	Boolean LOCAL_LOG = false;

	public CopyService() {
		super(name);
		if(PastySharedStatics.LOCAL_LOG == true) LOCAL_LOG = true;
	}
	
	@Override
	public void onCreate() {
	    super.onCreate();
	    mHandler = new Handler();
	}; 

	@SuppressLint("NewApi")
	@Override
	protected void onHandleIntent(Intent intent) {
		final Bundle extras = intent.getExtras();
		if(extras == null) {
			if(LOCAL_LOG) Log.w(TAG, "Empty Intent received. Exiting.");
			return;
		}
		String itemId = extras.getString("de.electricdynamite.pasty.itemId");
		String item = extras.getString("de.electricdynamite.pasty.item");
		Boolean notify = extras.getBoolean("de.electricdynamite.pasty.notify", false);
		if(itemId == null || item == null) {
			if(LOCAL_LOG) Log.w(TAG, "Invalid itemId or empty item. Exiting.");
			return;
		}
		ClipboardItem mItem = new ClipboardItem(itemId, item);
		if(LOCAL_LOG) Log.v(TAG, "Copying to clipboard");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			android.content.ClipboardManager sysClipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			mItem.copyToClipboard(sysClipboard);
			sysClipboard = null;
		} else {
			ClipboardManager sysClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			mItem.copyToClipboard(sysClipboard);
			sysClipboard = null;
		}
		if(notify == true) {
			mHandler.post(new Runnable() {            
				@Override
			    public void run() {
					Toast.makeText(CopyService.this, getString(R.string.item_copied), Toast.LENGTH_SHORT).show();         
			    }
			});
		}
	}

}
