package de.electricdynamite.pasty;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.ClipboardManager;

@SuppressWarnings("deprecation")
public class ClipboardItem {
	public String Id = "";
	public String ItemText = "";
	
	public ClipboardItem(String Id, String ItemText) {
		this.Id = Id;
		this.ItemText = ItemText;
	}
	
	public String getId() {
		return this.Id;
	}
	
	public String getText() {
		return this.ItemText;
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
