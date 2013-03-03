package de.electricdynamite.pasty;
/*
 *  Copyright 2012-2013 Philipp Geschke
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

@SuppressWarnings("deprecation")
public class GCMIntentService extends GCMBaseIntentService {
	public static final String TAG = GCMIntentService.class.toString();
	public static final int EVENT_ITEM_ADDED = 1;
	protected static final String CACHEFILE = PastySharedStatics.CACHEFILE;
	public boolean LOCAL_LOG = false;
	private PastyClient client;
	private PastyPreferencesProvider prefs;

	public GCMIntentService() {
		super(PastySharedStatics.GCM_SENDER_ID);
		if(PastySharedStatics.LOCAL_LOG == true) LOCAL_LOG = true;
	}

	@Override
	protected void onError(Context context, String errorId) {
		// TODO Auto-generated method stub
		Log.w(TAG,"onError(): "+errorId);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onMessage(Context context, Intent intent) {
		if(prefs == null) this.prefs = new PastyPreferencesProvider(context);
		final Bundle extras = intent.getExtras();
		if(extras == null) {
			Log.i(TAG, "onMessage(): Empty intent received");
			return;
		}
		final String mItemId = extras.getString("itemId");
		final String mItemStr = extras.getString("item");
		final int mEventId = Integer.parseInt(extras.getString("eventId"));
		if(mItemId == null || mItemStr == null || mItemId == "" || mItemStr == "") {
			Log.i(TAG, "onMessage(): Invalid intent received");
			return;
		}
		if(LOCAL_LOG) Log.v(TAG, "onMessage(): Received message for event: "+mEventId);
		switch(mEventId) {
		case EVENT_ITEM_ADDED:
			final String lastItemId = prefs.getLastItem(); 
			if(mItemId.equals(lastItemId)) return;
			if(client == null) {
		    	client = new PastyClient(prefs.getRESTBaseURL(), true);
		    	client.setUsername(prefs.getUsername());
		    	client.setPassword(prefs.getPassword());
			}

			final ClipboardItem mItem = new ClipboardItem(mItemId, mItemStr);
			final Boolean mPush = prefs.getPush();
			final Boolean mCopyToClipboard = prefs.getPushCopyToClipboard();
			final Boolean mNotify = prefs.getPushNotify();
			if(mPush == true) {
				if(mCopyToClipboard == true) {
					if(mItem.getText() != "") {
						Intent resultIntent = new Intent(this, CopyService.class);
						resultIntent.putExtra("de.electricdynamite.pasty.itemId", mItem.getId());
						resultIntent.putExtra("de.electricdynamite.pasty.item", mItem.getText());
						resultIntent.putExtra("de.electricdynamite.pasty.notify", mNotify);
						startService(resultIntent);
						resultIntent = null;
					}
				} else {
					if(mNotify == true) {
						String contentText = String.format(getString(R.string.notification_event_add_text), mItem.getText());
						NotificationCompat.Builder mBuilder =
						        new NotificationCompat.Builder(this)
						        .setSmallIcon(R.drawable.ic_stat_pasty)
						        .setContentTitle(getString(R.string.notification_event_add_title))
						        .setContentText(contentText)
						        .setAutoCancel(Boolean.TRUE);
						// Creates an explicit intent for an Activity in your app
						Intent resultIntent = new Intent(this, CopyService.class);
						resultIntent.putExtra("de.electricdynamite.pasty.itemId", mItem.getId());
						resultIntent.putExtra("de.electricdynamite.pasty.item", mItem.getText());
						resultIntent.putExtra("de.electricdynamite.pasty.notify", mNotify);
						PendingIntent resultPendingIntent = PendingIntent.getService(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
						mBuilder.setContentIntent(resultPendingIntent);
						NotificationManager mNotificationManager =
						    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
						// mId allows you to update the notification later on.
						mNotificationManager.notify(PastySharedStatics.NOTIFICATION_ID, mBuilder.build());
					}
				}
				JSONArray clipboard = null;
				try {
					clipboard = client.getClipboard();
					cacheClipboard(clipboard);
				} catch (PastyException e) {
					if(LOCAL_LOG) Log.v(TAG, "Could not get clipboard: "+e.getMessage());
				}
				
			}
			break;
		default:
			Log.i(TAG,"onMessage(): Unsupported event: "+mEventId);
			break;
		}
		/* TODO Make ClipboardFragment react to changes from GCMIntentService */
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		if(LOCAL_LOG) Log.v(TAG, "GCM: Registered as "+regId);
		this.prefs = new PastyPreferencesProvider(context);
        // Create a PastyClient
    	client = new PastyClient(prefs.getRESTBaseURL(), true);
    	client.setUsername(prefs.getUsername());
    	client.setPassword(prefs.getPassword());
    	try {
			client.registerDevice(regId);
			prefs.setRegisterError(false);
		} catch (PastyException e) {
			if(e.errorId != PastyException.ERROR_DEVICE_ALREADY_REGISTERED) {
				Log.w(TAG,"GCMIntentService.onRegistered(): Failed to submit regId to API server");
				e.printStackTrace();
				prefs.setRegisterError();
			}
		}
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		if(LOCAL_LOG) Log.v(TAG, "GCM: Unregistered "+regId);
		this.prefs = new PastyPreferencesProvider(context);
        // Create a PastyClient
    	client = new PastyClient(prefs.getRESTBaseURL(), true);
    	client.setUsername(prefs.getUsername());
    	client.setPassword(prefs.getPassword());
    	if(regId != "") {
    		try {
    			client.unregisterDevice(regId);
    		} catch (PastyException e) {
    			if(e.errorId != PastyException.ERROR_DEVICE_NOT_REGISTERED) {
    				Log.w(TAG,"GCMIntentService.onUnregistered(): Failed to unregister from API server");
    				e.printStackTrace();
    			}
    		}
    	}

	}
	
	private void cacheClipboard(JSONArray clipboard) {
    	File mDeviceCacheFile = new File(
                this.getCacheDir(), CACHEFILE);

        try {
        	mDeviceCacheFile.createNewFile();
            FileWriter fw = new FileWriter(mDeviceCacheFile);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(clipboard.toString());
            bw.newLine();
            bw.close();
            if (LOCAL_LOG) Log.v(TAG, "cacheClipboard(): Saved result to cache");

        } catch (IOException e) {
        	Log.w(TAG, "cacheClipboard(): Could not create cache file");
        }
    }

}
