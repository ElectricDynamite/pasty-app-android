About Permissions used
======================

Below is a list of all Permissions Pasty for Android&trade; currently uses and why and how these permissions are used.

android.permission.INTERNET / Full Network Access
-------------------------------------------------
This permission is used for the core funtionality of providing an online clipboard.  
In order to communicate with Pasty's server to retrieve the clipboard, Internet access is required.

android.permission.WAKE_LOCK / Prevent Phone from Sleeping
----------------------------------------------------------
This is necessary in order to use [Google Cloud Messaging][1], which is used to push
clipboard items to your device while using as little battery life as possible.  
_Only used when push is activated_

android.permission.ACCESS\_NETWORK\_STATE / Receive data from Internet, view network connections
-----------------------------------------------------------------------------------------------
Used in order to see if a network connection is available before accessing the Internet

android.permission.GET_ACCOUNTS / Find accounts on the device
-------------------------------------------------------------
This is necessary in order to use [Google Cloud Messaging][1], which is used to push
clipboard items to your device while using as little battery life as possible.  

No personal or account information is actually retrieved, besides a device registration ID for GCM.  
_Only used when push is activated_

com.google.android.c2dm.permission.RECEIVE
------------------------------------------
This is necessary in order to use [Google Cloud Messaging][1], which is used to push
clipboard items to your device while using as little battery life as possible.  
_Only used when push is activated_

de.electricdynamite.pasty.permission.C2D_MESSAGE
------------------------------------------------
Allows Pasty to receive its own messages when using [Google Cloud Messaging][1], which is used to push
clipboard items to your device while using as little battery life as possible.  
_Only used when push is activated_


[1]: http://developer.android.com/google/gcm/gs.html
