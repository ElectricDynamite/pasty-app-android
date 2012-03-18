package de.electricdynamite.pasty;

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
	
}
