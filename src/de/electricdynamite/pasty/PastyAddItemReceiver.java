package de.electricdynamite.pasty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PastyAddItemReceiver extends BroadcastReceiver {
	@Override 
	public void onReceive(Context _context, Intent _intent) {
		if (_intent.getAction().equals(Intent.ACTION_SEND)) {
	      // TODO Broadcast a notification
			
	    }
	 } 
}
