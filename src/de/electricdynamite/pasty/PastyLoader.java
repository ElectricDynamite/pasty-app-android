package de.electricdynamite.pasty;

import org.json.JSONArray;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public class PastyLoader extends AsyncTaskLoader<PastyLoader.PastyResponse> {
    private static final String TAG = PastyLoader.class.getName();
    
    // Delta to determine if our cached response is old
	private static final long STALE_DELTA = 60000;

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
    
	private PastyResponse mCachePastyResponse;
    
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
		Log.d(TAG, "loadInBackground() called");
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
		// Store our PastyResponse as cached result
		mCachePastyResponse = response;
        super.deliverResult(response);
    }
    
    @Override
    protected void onStartLoading() {
    	if (mCachePastyResponse != null) {
    		// Instantly return a cached version
    		super.deliverResult(mCachePastyResponse);
    	}
    
    	// If we have not response or only an old response we will forceLoad();
    	if (mCachePastyResponse == null || System.currentTimeMillis() - mLastLoad >= STALE_DELTA) forceLoad();
    	mLastLoad = System.currentTimeMillis();
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
        
        // Reset cache
        mCachePastyResponse = null;
                
        // Reset our stale timer.
        mLastLoad = 0;
    }

}