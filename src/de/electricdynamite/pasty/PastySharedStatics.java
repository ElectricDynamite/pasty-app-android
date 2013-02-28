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


public class PastySharedStatics {
	
	// Development Mode
	static final boolean LOCAL_LOG = true; 
	
	// GCM
	static final String GCM_SENDER_ID = "677943136875";

	// Error Dialog IDs
    static final int DIALOG_CONNECTION_ERROR_ID	= 1;
    static final int DIALOG_AUTH_ERROR_ID		= 2;
    static final int DIALOG_CREDENTIALS_NOT_SET = 3;
    static final int DIALOG_NO_NETWORK			= 4;
    static final int DIALOG_BAD_ANSWER			= 5;
    static final int DIALOG_UNKNOWN_ERROR_ID	= 99;   		

    // Other Dialog IDs
    static final int DIALOG_ABOUT_ID			= 101;
    static final int DIALOG_ADD_ID				= 102;
    static final int DIALOG_NOT_SUPPORTED_ID	= 127;
    
    // Item Context Menu IDs
    static final int ITEM_CONTEXTMENU_COPY_ID	= 0;
    static final int ITEM_CONTEXTMENU_SHARE_ID	= 1;
    static final int ITEM_CONTEXTMENU_DELETE_ID	= 2;
    static final int ITEM_CONTEXTMENU_OPEN_ID	= 3;
    
    static final String PREF_USER				= "pref_username";  
    static final String PREF_PASSWORD			= "pref_password"; 
    static final String PREF_HTTPS				= "pref_usehttps";
    static final String PREF_SERVER			= "pref_server";
    static final String PREF_LAST_ITEM			= "pref_last_item";
    static final String PREF_PASTE_CLIPBOARD	= "pref_paste_clipboard";
    static final String PREF_CLICKABLE_LINKS	= "pref_clickable_links";
    static final String PREF_PUSH_GCM			= "pref_push_gcm";
    static final String PREF_PUSH_COPY_TO_CLIPBOARD = "pref_push_copy_to_clipboard";
    static final String PREF_PUSH_NOTIFY		= "pref_push_notify";
    
//    static final String DEFAULT_REST_URI_HTTP	= "http://api.pastyapp.org/";
//    static final String DEFAULT_REST_URI_HTTPS	= "https://api.pastyapp.org/";
    static final String DEFAULT_REST_URI_HTTP	= "http://mario.blafaselblub.net:8080/";
    static final String DEFAULT_REST_URI_HTTPS	= "https://mario.blafaselblub.net:4444/";
    //static final String DEFAULT_REST_URI_HTTP 	= "http://10.10.10.10:8888";
    
    // Dialog Signal
    static final int SIGNAL_EXIT = 0x1;
    static final int SIGNAL_ACTIVITY_SETTINGS = 0xA1;
    static final int SIGNAL_ACTIVITY_ABOUT = 0xA2;
    static final int SIGNAL_ERROR = 0xB1;
    
	static final int NOTIFICATION_ID = 0x3B;
    
    static final String CACHEFILE = "ClipboardCache.json";
}
