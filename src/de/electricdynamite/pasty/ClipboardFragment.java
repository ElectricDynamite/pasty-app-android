package de.electricdynamite.pasty;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.ClipboardManager;
import android.text.util.Linkify;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;

import de.electricdynamite.pasty.PastyLoader.PastyResponse;

public class ClipboardFragment extends SherlockListFragment implements LoaderCallbacks<PastyLoader.PastyResponse> {
	private static final String TAG = ClipboardFragment.class.toString();
	private LayoutInflater mInflater;
	private Resources mRes;
	private ClipboardItemListAdapter mAdapter;
	private ArrayList<ClipboardItem> mItems;

	private boolean mFirstRun = true;
	private final Handler mHandler = new Handler();

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// this is really important in order to save the state across screen
		// configuration changes for example
		setRetainInstance(true);

		// LoaderManager.enableDebugLogging(true);

		mRes = getResources();
		mInflater = LayoutInflater.from(getSherlockActivity());


		// you only need to instantiate these the first time your fragment is
		// created; then, the method above will do the rest
		if (mAdapter == null) {
			mItems = new ArrayList<ClipboardItem>();
			mAdapter = new ClipboardItemListAdapter(getSherlockActivity(), mItems);
		}
		getListView().setAdapter(mAdapter);

		// ---- magic lines starting here -----
		// call this to re-connect with an existing
		// loader (after screen configuration changes for e.g!)
		LoaderManager lm = getSherlockActivity().getSupportLoaderManager();
		if (lm.getLoader(PastyLoader.TASK_CLIPBOARD_FETCH) != null) {
			Log.d(TAG, "onActivityCreated(): Loader already exists, reconnecting");
			lm.initLoader(PastyLoader.TASK_CLIPBOARD_FETCH, null, this);
		}
		// ----- end magic lines -----

		if(mFirstRun) {
			startLoading();
		}
	}

	protected void startLoading() {
		Log.d(TAG,"startLoading()");
		//showDialog();
		getSherlockActivity().setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
		TextView mHelpTextBig			= (TextView) getSherlockActivity().findViewById(R.id.tvHelpTextBig);
		ProgressBar pbLoading			= (ProgressBar) getSherlockActivity().findViewById(R.id.progressbar_downloading);
		mHelpTextBig.setText(R.string.helptext_PastyActivity_loading);
		pbLoading.setVisibility(View.VISIBLE);
		mHelpTextBig = null;
		pbLoading = null;
		Bundle b = new Bundle();
		
		// first time we call this loader, so we need to create a new one
		getLoaderManager().initLoader(PastyLoader.TASK_CLIPBOARD_FETCH, b, this);
		b = null;
		getSherlockActivity().setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
	}
	
	protected void restartLoading() {
		//showDialog();


		getSherlockActivity().setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
		// --------- the other magic lines ----------
		// call restart because we want the background work to be executed
		// again
		Log.d(TAG, "restartLoading(): re-starting loader");
		Bundle b = new Bundle();
		getLoaderManager().restartLoader(PastyLoader.TASK_CLIPBOARD_FETCH, b, this);
		b = null;
		// --------- end the other magic lines --------
	}

	
	@Override
	public Loader<PastyLoader.PastyResponse> onCreateLoader(int id, Bundle args) {
		return new PastyLoader(getSherlockActivity(), id);
	}

	@Override
	public void onLoadFinished(Loader<PastyLoader.PastyResponse> loader, PastyLoader.PastyResponse response) {
		ProgressBar pbLoading			= (ProgressBar) getSherlockActivity().findViewById(R.id.progressbar_downloading);
		pbLoading.setVisibility(View.GONE);
		pbLoading = null;
		getSherlockActivity().setSupportProgressBarIndeterminateVisibility(Boolean.FALSE); 
	    if(response.hasException) {
	    	Log.d(TAG, "Loader delivered exception");
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
	    		Log.d(TAG, "Loader delivered TASK_CLIPBOARD_FETCH without exception");
	    		JSONArray Clipboard = response.getClipboard();
	    		mItems.clear();
	    		mAdapter.notifyDataSetChanged();
	    		getListView().invalidateViews();
	    		try {
	    		    if(Clipboard.length() == 0) {
	    		       //Clipboard is empty
	    	        	TextView mHelpTextBig = (TextView) getSherlockActivity().findViewById(R.id.tvHelpTextBig);
	    	        	mHelpTextBig.setText(R.string.helptext_PastyActivity_clipboard_empty);
	    	        	mHelpTextBig = null;
	    	        	TextView mHelpTextSmall = (TextView) getSherlockActivity().findViewById(R.id.tvHelpTextSmall);
	    	        	mHelpTextSmall.setText(R.string.helptext_PastyActivity_how_to_add);
	    	        	mHelpTextSmall = null;
	    	        } else {
	    				if(Clipboard.length() > 15) {
	    					throw new Exception();
	    				}
	    				for (int i = 0; i < Clipboard.length(); i++) {
	    					JSONObject Item = Clipboard.getJSONObject(i);
	    					ClipboardItem cbItem = new ClipboardItem(Item.getString("_id"), Item.getString("item"));
	    					this.mItems.add(cbItem);
	    				}
	    			
	    				TextView mHelpTextBig = (TextView) getSherlockActivity().findViewById(R.id.tvHelpTextBig);
	    				mHelpTextBig.setText(R.string.helptext_PastyActivity_copy);
	    				mHelpTextBig = null;
	    			
	    				//Assign adapter to ListView
	    				ListView listView = (ListView) getSherlockActivity().findViewById(R.id.listItems);
	    				listView.setAdapter(mAdapter);
	    				listView.setOnItemClickListener(new OnItemClickListener() { 
	    					@Override
	    					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	    	    				Log.d(TAG, "listView.onItemClick() called");
	    				    	ClipboardItem Item = mItems.get(position);
	    						ClipboardManager sysClipboard = (ClipboardManager) getSherlockActivity().getSystemService(Context.CLIPBOARD_SERVICE);
	    						Item.copyToClipboard(sysClipboard);
	    				    	Context context = getSherlockActivity().getApplicationContext();
	    				    	CharSequence text = getString(R.string.item_copied);
	    				    	int duration = Toast.LENGTH_LONG;
	    				    	Toast toast = Toast.makeText(context, text, duration);
	    				    	toast.show();
	    				    	toast = null;
	    				    	context = null;
	    				    	sysClipboard = null;
	    				    	text = null;
	    					    getSherlockActivity().finish();
	    					}
	    				});
	    				registerForContextMenu(listView);
	    		    }
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
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
			// List of stored ClipboardItems
	        private List<ClipboardItem> itemList;
	        private Context context;
	     
	        public ClipboardItemListAdapter(Context context, List<ClipboardItem> itemList) {
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
				
				View view = convertView;
				Wrapper wrapper;

				if (view == null) {
					view = mInflater.inflate(R.layout.listitem, parent, false);
					wrapper = new Wrapper(view);
					view.setTag(wrapper);
				} else {
					wrapper = (Wrapper) view.getTag();
				}
				
	            //LinearLayout itemLayout;
	            ClipboardItem Item = itemList.get(position);
	     
	            //itemLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.listitem, parent, false);
	     
	            TextView tvListItem = (TextView) view.findViewById(R.id.myListitem);
	            tvListItem.setText(Item.getText());
	            Linkify.addLinks(tvListItem, Linkify.ALL);
	     
	     
	            return view;
	        }
	    }
	
		// use an wrapper (or view holder) object to limit calling the
		// findViewById() method, which parses the entire structure of your
		// XML in search for the ID of your view
		private static class Wrapper {
			private final View mRoot;
			private TextView tvHelpTextBig;
			private TextView tvHelpTextSmall;
			private ProgressBar pbLoading;
			
			public static final int VIEW_HELPTEXT_BIG = 0x1;
			public static final int VIEW_HELPTEXT_SMALL = 0x2;
			public static final int VIEW_PROGRESS_LOADING = 0x3;

			public Wrapper(View root) {
				mRoot = root;
			}

			public TextView getTextView(int tv) {
				switch (tv) {
				case VIEW_HELPTEXT_BIG:
					if (tvHelpTextBig == null) {
						tvHelpTextBig = (TextView) mRoot.findViewById(R.id.tvHelpTextBig);
					}
					return tvHelpTextBig;
				case VIEW_HELPTEXT_SMALL:
					if (tvHelpTextSmall == null) {
						tvHelpTextSmall = (TextView) mRoot.findViewById(R.id.tvHelpTextSmall);
					}
					return tvHelpTextBig;
				default:
					return null;
				}
			}

			public View getBar() {
				if (pbLoading == null) {
					pbLoading = (ProgressBar) mRoot.findViewById(R.id.progressbar_downloading);
				}
				return pbLoading;
			}
		}
		
		@Override
	    public void onCreateContextMenu(ContextMenu menu, View v,
	        ContextMenuInfo menuInfo) {
	      if (v.getId()==R.id.listItems) {
	        menu.setHeaderTitle(getString(R.string.itemContextMenuTitle));
	        String[] menuItems = getResources().getStringArray(R.array.itemContextMenu);
	        for (int i = 0; i<menuItems.length; i++) {
	          menu.add(Menu.NONE, i, i, menuItems[i]);
	        }
	      }      
	    }
	    
	    public boolean onContextItemSelected(android.view.MenuItem item) {
	      AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	      int menuItemIndex = item.getItemId();
	      ClipboardItem Item = mItems.get(info.position);
	      switch (menuItemIndex) {
	      	case PastySharedStatics.ITEM_CONTEXTMENU_COPY_ID:
	      		// Copy without exit selected
	      		ClipboardManager clipboard = (ClipboardManager) getSherlockActivity().getSystemService(Context.CLIPBOARD_SERVICE);
				Item.copyToClipboard(clipboard);
		    	Context context = getSherlockActivity().getApplicationContext();
		    	CharSequence text = getString(R.string.item_copied);
		    	int duration = Toast.LENGTH_LONG;
		    	Toast toast = Toast.makeText(context, text, duration);
		    	toast.show();
		    	toast = null;
		    	context = null;
		    	clipboard = null;
		    	text = null;
	      		break;
	      	case PastySharedStatics.ITEM_CONTEXTMENU_SHARE_ID:
	      		// Share to another app
	      		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
	      		shareIntent.setType("text/plain");
	      		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, Item.getText());
	      		startActivity(Intent.createChooser(shareIntent, getString(R.string.app_share_from_pasty)));
	      		break;
	      	case PastySharedStatics.ITEM_CONTEXTMENU_DELETE_ID:
	      		// Delete selected
	      		//this.deleteItem(Item, info.position);
	      		break;
	      }
	      //TextView text = (TextView)findViewById(R.id.footer);
	      //text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));
	      return true;
	    }
}
