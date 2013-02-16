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
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class GCMIntentService extends GCMBaseIntentService {
	public static final String TAG = GCMIntentService.class.toString();
	public static final int EVENT_ITEM_ADDED = 1;
	public boolean LOCAL_LOG = true;
	private PastyClient client;
	private PastyPreferencesProvider prefs;

	public GCMIntentService() {
		super(PastySharedStatics.GCM_SENDER_ID);
	}

	@Override
	protected void onError(Context context, String errorId) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		final Bundle extras = intent.getExtras();
		final int mEventId = extras.getInt("eventId");
		if(LOCAL_LOG) Log.v(TAG, "GCM: Received message for event: "+mEventId);
		switch(mEventId) {
		case EVENT_ITEM_ADDED:
			
			if(client == null) {
		    	client = new PastyClient(prefs.getRESTBaseURL(), true);
		    	client.setUsername(prefs.getUsername());
		    	client.setPassword(prefs.getPassword());
			}
			if(prefs.getPush() == PastyPreferencesProvider.PUSH_TO_CLIPBOARD) {
				Log.v(TAG, extras.getString("item"));
				/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					android.content.ClipboardManager sysClipboard = (android.content.ClipboardManager) getSherlockActivity().getSystemService(Context.CLIPBOARD_SERVICE);
					Item.copyToClipboard(sysClipboard);
					sysClipboard = null;
				} else {
					ClipboardManager sysClipboard = (ClipboardManager) getSherlockActivity().getSystemService(Context.CLIPBOARD_SERVICE);
					Item.copyToClipboard(sysClipboard);
					sysClipboard = null;
				}*/
			}
			break;
		default:
			break;
		}
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
		} catch (PastyException e) {
			if(e.errorId != PastyException.ERROR_DEVICE_ALREADY_REGISTERED) {
				Log.w(TAG,"GCMIntentService.onRegistered(): Failed to submit regId to API server");
				//GCMRegistrar.unregister(context);
				e.printStackTrace();
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

}
