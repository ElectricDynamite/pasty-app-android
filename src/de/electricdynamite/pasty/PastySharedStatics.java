package de.electricdynamite.pasty;

public class PastySharedStatics {

	// Error Dialog IDs
    static final int DIALOG_CONNECTION_ERROR_ID	= 1;
    static final int DIALOG_AUTH_ERROR_ID		= 2;
    static final int DIALOG_CREDENTIALS_NOT_SET = 3;
    static final int DIALOG_UNKNOWN_ERROR_ID	= 99;   		

    // Other Dialog IDs
    static final int DIALOG_ABOUT_ID			= 101;
    static final int DIALOG_ADD_ID				= 102;
    static final int DIALOG_NOT_SUPPORTED_ID	= 127;
    
    // Item Context Menu IDs
    static final int ITEM_CONTEXTMENU_COPY_ID	= 0;
    static final int ITEM_CONTEXTMENU_SHARE_ID	= 1;
    static final int ITEM_CONTEXTMENU_DELETE_ID	= 2;
    
    static final String PREF_USER				= "pref_username";  
    static final String PREF_PASSWORD			= "pref_password"; 
    static final String PREF_HTTPS				= "pref_usehttps";
    static final String PREF_SERVER				= "pref_server";
    static final String PREF_PASTE_CLIPBOARD	= "pref_paste_clipboard";
    
    static final String PREF_SERVER_DEFAULT		= "mario.blafaselblub.net";
    
    static final String PORT_HTTP				= "80";
    static final String PORT_HTTPS				= "4444";
    
    static final String PASTY_REST_URI_ITEM		= "/v1/clipboard/item/";
    static final String PASTY_REST_URI_CLIPBOARD= "/v1/clipboard/list.json";
}
