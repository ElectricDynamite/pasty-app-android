package de.electricdynamite.pasty;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.ClipboardManager;

@SuppressWarnings("deprecation")
public class ClipboardItem {
	private String Id = "";
	private String ItemText = "";
	private Boolean isLinkified = false;
	
	public ClipboardItem(String Id, String ItemText) {
		this.Id = Id;
		this.ItemText = ItemText;
	}
	
	public void linkfied() {
		this.isLinkified = true;
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
		jsItem.put("i", this.ItemText);
		jsItem.put("_id", this.Id);
		return jsItem;
	}
	
	public void copyToClipboard(ClipboardManager clipboard) {
    	clipboard.setText(this.getText());
	}
}
