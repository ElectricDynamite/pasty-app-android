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
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
	public static final String TAG = GCMIntentService.class.toString();
	public boolean LOCAL_LOG = true;

	@Override
	protected void onError(Context context, String errorId) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		if(LOCAL_LOG) Log.v(TAG, "GCM: Received message");

	}

	@Override
	protected void onRegistered(Context context, String regId) {
		if(LOCAL_LOG) Log.v(TAG, "GCM: Registered as "+regId);

	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		// TODO Auto-generated method stub

	}

}
