package de.electricdynamite.pasty;

import org.json.JSONArray;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public class PastyLoader extends AsyncTaskLoader<PastyLoader.PastyResponse> {
    private static final String TAG = PastyLoader.class.getName();
    
    // We use this delta to determine if our cached data is
    // old or not. The value we have here is 10 minutes;
    @SuppressWarnings("unused")
	private static final long STALE_DELTA = 600000;

	private PastyClient client;
	private PastyPreferencesProvider prefs;
    
    public static final int TASK_CLIPBOARD_FETCH = 0xA1;
    public static final int TASK_ITEM_ADD = 0xB1;
    public static final int TASK_ITEM_DELETE = 0xB2;
    
    private int taskId = 0x0;
    private ClipboardItem item;
    
    
    public static class PastyResponse {
        private JSONArray mClipboard;
        private PastyException mException;
		public boolean hasException = false;

		public PastyResponse() {
        }
        
        public PastyResponse(JSONArray clipboard) {
        	this.mClipboard = clipboard;
        }
        
        public PastyResponse(PastyException e) {
        	this.mException = e;
        	this.hasException  = true;
        }
        
        public JSONArray getClipboard() {
            return mClipboard;
        }
        
        public PastyException getException() {
        	return this.mException;
        }
    }
    
    @SuppressWarnings("unused")
	private PastyResponse mPastyResponse;
    
    @SuppressWarnings("unused")
	private long mLastLoad;
        
    public PastyLoader(Context context, int taskId) {
        super(context);
    	// Restore preferences
   	 	this.prefs = new PastyPreferencesProvider(context);
        // Create a PastyClient
    	client = new PastyClient(prefs.getRESTBaseURL(), true);
    	client.setUsername(prefs.getUsername());
    	client.setPassword(prefs.getPassword());
        this.taskId = taskId;
    }
    
    public PastyLoader(Context context, int taskId, ClipboardItem item) {
        super(context);
        Log.d(TAG, "Loader constructor called");
    	// Restore preferences
   	 	this.prefs = new PastyPreferencesProvider(context);
        // Create a PastyClient
    	client = new PastyClient(prefs.getRESTBaseURL(), true);
    	client.setUsername(prefs.getUsername());
    	client.setPassword(prefs.getPassword());
        this.taskId = taskId;
        this.item = item;
    }

	@Override
    public PastyResponse loadInBackground() {
        try {
            // At the very least we always need an action.
            if (taskId == 0x0) {
                Log.e(TAG, "No taskId provided.");
                return new PastyResponse(); 
            }
            switch(taskId) {
            case TASK_CLIPBOARD_FETCH:
            	JSONArray clipboard = client.getClipboard();
            	return new PastyResponse(clipboard);
            case TASK_ITEM_ADD:
            	break;
            case TASK_ITEM_DELETE:
            	break;
            default:
            	return new PastyResponse();
            }            
            // Request was null if we get here, so let's just send our empty RESTResponse like usual.
            return new PastyResponse();
        } 
        catch (PastyException e) {
        	return new PastyResponse(e);
        }
    }
    
	@Override
    public void deliverResult(PastyResponse response) {
        super.deliverResult(response);
    }
    
    @Override
    protected void onStartLoading() {
    	forceLoad();
    }
    
    @Override
    protected void onStopLoading() {
        // This prevents the AsyncTask backing this
        // loader from completing if it is currently running.
        cancelLoad();
    }
    
    @Override
    protected void onReset() {
        super.onReset();
        
        // Stop the Loader if it is currently running.
        onStopLoading();
                
        // Reset our stale timer.
        mLastLoad = 0;
    }

}