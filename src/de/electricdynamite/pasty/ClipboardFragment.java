package de.electricdynamite.pasty;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.ClipboardManager;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockListFragment;

import de.electricdynamite.pasty.PastyLoader.PastyResponse;

public class ClipboardFragment extends SherlockListFragment implements LoaderCallbacks<PastyLoader.PastyResponse> {
	private static final String TAG = ClipboardFragment.class.toString();
	

	private List<ClipboardItem> ItemList = new ArrayList<ClipboardItem>();
	private ClipboardItemListAdapter ClipboardListAdapter;
	
	@Override
	public Loader<PastyLoader.PastyResponse> onCreateLoader(int id, Bundle args) {
		return new PastyLoader(getActivity(), id);
	}

	@Override
	public void onLoadFinished(Loader<PastyLoader.PastyResponse> loader, PastyLoader.PastyResponse response) {
	    if(response.hasException) {
	    	// an error occured
	    	PastyException mException = response.getException();
	    	switch(mException.errorId) {
	    		case PastyException.ERROR_AUTHORIZATION_FAILED:
					//showDialog(PastySharedStatics.DIALOG_AUTH_ERROR_ID);
	    			Log.d(TAG, "ERROR_AUTHORIZATION_FAILED EXCEPTION");
					return;
				case PastyException.ERROR_IO_EXCEPTION:
					//showDialog(PastySharedStatics.DIALOG_CONNECTION_ERROR_ID);
	    			Log.d(TAG, "ERROR_IO_EXCEPTION");
					return;
				case PastyException.ERROR_ILLEGAL_RESPONSE:
					//showDialog(PastySharedStatics.DIALOG_BAD_ANSWER);
	    			Log.d(TAG, "ERROR_ILLEGAL_RESPONSE EXCEPTION");
					return;
				case PastyException.ERROR_UNKNOWN:
					//showDialog(PastySharedStatics.DIALOG_UNKNOWN_ERROR_ID);
					//setSupportProgressBarIndeterminateVisibility(Boolean.FALSE); TODO Manipulate Activity 
					//ProgressBar pbLoading			= (ProgressBar) findViewById(R.id.progressbar_downloading);
					//pbLoading.setVisibility(View.GONE);
					//pbLoading = null;
					//TextView mHelpTextBig = (TextView) findViewById(R.id.tvHelpTextBig);
					//mHelpTextBig.setText(R.string.helptext_PastyActivity_error_occured);
					//mHelpTextBig = null;
					return;
				default:
					break;
				}
	    } else {
	    	switch(loader.getId()) {
	    	case PastyLoader.TASK_CLIPBOARD_FETCH:
	    		/*ProgressBar pbLoading			= (ProgressBar) findViewById(R.id.progressbar_downloading);
	    		pbLoading.setVisibility(View.GONE);
	    		pbLoading = null;*/
	    		JSONArray Clipboard = response.getClipboard();
	    		try {
	    		    if(Clipboard.length() == 0) {
	    		       //Clipboard is empty
	    	        	/*TextView mHelpTextBig = (TextView) findViewById(R.id.tvHelpTextBig);
	    	        	mHelpTextBig.setText(R.string.helptext_PastyActivity_clipboard_empty);
	    	        	mHelpTextBig = null;
	    	        	TextView mHelpTextSmall = (TextView) findViewById(R.id.tvHelpTextSmall);
	    	        	mHelpTextSmall.setText(R.string.helptext_PastyActivity_how_to_add);
	    	        	mHelpTextSmall = null;*/
	    	        } else {
	    				if(Clipboard.length() > 15) {
	    					throw new Exception();
	    				}
	    				for (int i = 0; i < Clipboard.length(); i++) {
	    					JSONObject Item = Clipboard.getJSONObject(i);
	    					ClipboardItem cbItem = new ClipboardItem(Item.getString("_id"), Item.getString("item"));
	    					this.ItemList.add(cbItem);
	    				}
	    			
	    				/*TextView mHelpTextBig = (TextView) findViewById(R.id.tvHelpTextBig);
	    				mHelpTextBig.setText(R.string.helptext_PastyActivity_copy);
	    				mHelpTextBig = null;*/
	    			
	    				ClipboardItemListAdapter adapter = new ClipboardItemListAdapter(this.ItemList, getActivity());
	    				//Assign adapter to ListView
	    				/*ListView listView = (ListView) findViewById(R.id.listItems);
	    				listView.setAdapter(adapter);*/
	    				this.ClipboardListAdapter = adapter;
	    					
	    				/*listView.setOnItemClickListener(new OnItemClickListener() { 
	    					@Override
	    					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    				    	ClipboardItem Item = PastyActivity.this.ItemList.get(position);
	    						ClipboardManager sysClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
	    						Item.copyToClipboard(sysClipboard);
	    				    	Context context = getApplicationContext();
	    				    	CharSequence text = getString(R.string.item_copied);
	    				    	int duration = Toast.LENGTH_LONG;
	    				    	Toast toast = Toast.makeText(context, text, duration);
	    				    	toast.show();
	    				    	toast = null;
	    				    	context = null;
	    				    	sysClipboard = null;
	    				    	text = null;
	    					    PastyActivity.this.finish();
	    					}
	    				});
	    				registerForContextMenu(listView);*/
	    		    }
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
	    		// setSupportProgressBarIndeterminateVisibility(Boolean.FALSE); TODO
	    		break;
	    	default:
	    		break;
	    	}
	    }
	}

	@Override
	public void onLoaderReset(Loader<PastyResponse> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	 public class ClipboardItemListAdapter extends BaseAdapter {
    	 
	        private List<ClipboardItem> itemList;
	     
	        private Context context;
	     
	        public ClipboardItemListAdapter(List<ClipboardItem> itemList, Context context) {
	            this.itemList = itemList;
	            this.context = context;
	        }
	     
	        public int getCount() {
	            return itemList.size();
	        }
	     
	        public ClipboardItem getItem(int position) {
	            return itemList.get(position);
	        }
	        
	        public long getItemId(int position) {
	            return position;
	        }
	        
	        public String getClipboardItemId(int position) {
	        	return itemList.get(position).getId();
	        }
	        
	        public void remove(int position) {
	        	this.itemList.remove(position);
	        }
	        
	        public void removeAll() {
	        	this.itemList.clear();
	        }

			@Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	            LinearLayout itemLayout;
	            ClipboardItem Item = itemList.get(position);
	     
	            itemLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.listitem, parent, false);
	     
	            TextView tvListItem = (TextView) itemLayout.findViewById(R.id.myListitem);
	            tvListItem.setText(Item.getText());
	            Linkify.addLinks(tvListItem, Linkify.ALL);
	     
	     
	            return itemLayout;
	        }
	    }
}
