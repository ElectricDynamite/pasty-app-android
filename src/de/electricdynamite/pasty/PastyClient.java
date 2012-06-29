package de.electricdynamite.pasty;

public class PastyClient {
	/*public static final String	REST_SERVER_NAME	= "api.pastyapp.org";
	public static final int 	REST_SERVER_HTTP	= 80;
	public static final int		REST_SERVER_HTTPS	= 443;*/
	
	
	public static final String	REST_SERVER_NAME	= "mario.blafaselblub.net";
	public static final int 	REST_SERVER_HTTP	= 8080;
	public static final int		REST_SERVER_HTTPS	= 4444;

    static final String			REST_URI_ITEM		= "/v2/clipboard/item/";
    static final String 		REST_URI_CLIPBOARD	= "/v2/clipboard/list.json";
	
	public static final int		API_VERSION			= 2;
	
	private String username = "";
	private String password = "";
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean deleteItem(ClipboardItem Item) {
		String mURL = "https://"+REST_SERVER_NAME+":"+REST_SERVER_HTTPS+REST_URI_ITEM;
		String mMethod = "DELETE";
		return true;
	}
	
	
}
