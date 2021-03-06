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

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.ClipboardManager;


@SuppressWarnings("deprecation")
public class ClipboardItem {
	private static final String CLIP_LABEL = "Pasty";
	private static final String TAG = ClipboardItem.class.toString();
	private String Id = "";
	private String ItemText = "";
	private Boolean isLinkified = false;
	
	public ClipboardItem(String Id, String ItemText) {
		this.Id = Id;
		this.ItemText = ItemText;
	}
	
	public void setLinkfied(Boolean bool) {
		this.isLinkified = bool;
	}
	
	public String getId() {
		return this.Id;
	}
	
	public String getText() {
		return this.ItemText;
	}
	
	public Boolean isLinkified() {
		return isLinkified;
	}
	
	public JSONObject getJSON() throws JSONException {
		JSONObject jsItem = new JSONObject();
		jsItem.put("item", this.ItemText);
		jsItem.put("_id", this.Id);
		return jsItem;
	}
	
	public void copyToClipboard(ClipboardManager clipboard) {
    	clipboard.setText(this.getText());
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void copyToClipboard(android.content.ClipboardManager clipboard) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			clipboard.setPrimaryClip(android.content.ClipData.newPlainText(ClipboardItem.CLIP_LABEL, this.getText()));
		} else {
			android.util.Log.w(TAG, "copyToClipboard(): Modern version ClipboardManager API used one API <= 10");
		}
	}
}
