package de.electricdynamite.pasty;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;

import de.electricdynamite.pasty.PastyAlertDialogFragment.PastyAlertDialogListener;
import de.electricdynamite.pasty.PastyLoader.PastyResponse;


public class AddItemFragment extends SherlockDialogFragment {
	private static final String TAG = AddItemFragment.class.toString();
	protected PastyPreferencesProvider prefs;
	protected Context context;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		getDialog().setTitle(R.string.dialog_item_add_title);
	    View v = inflater.inflate(R.layout.add_item, container, false);
	    
	    Button button = (Button)v.findViewById(R.id.button_add_item_confirm);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
				LinearLayout mDialogLayout = (LinearLayout) v.getParent();
				EditText mNewItemET = (EditText) mDialogLayout.findViewById(R.id.NewItem);
				String mItem = mNewItemET.getText().toString();
            	new ItemAddTask().execute(mItem);
            }
        });
        return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.context = getSherlockActivity().getBaseContext();
    	this.prefs = new PastyPreferencesProvider(getSherlockActivity().getBaseContext());
	}
	

	public interface AddItemFragmentCallbackListener {
        void onAddItemFragmentCallbackSignal(int signal);
    }
	
	private class ItemAddTask extends AsyncTask<String, Void, PastyResponse > {
	    /** The system calls this to perform work in a worker thread and
	      * delivers it the parameters given to AsyncTask.execute() */
		@Override
		protected PastyResponse doInBackground(String... item) {
			PastyClient client = new PastyClient(prefs.getRESTBaseURL(), true);
			client.setUsername(prefs.getUsername());
			client.setPassword(prefs.getPassword());
			PastyResponse result;
			try {
				client.addItem(item[0]);
				result = new PastyResponse();
			} catch (PastyException e) {
				result = new PastyResponse(e);
			}
			return result;
		}
	    
	    /** The system calls this to perform work in the UI thread and delivers
	      * the result from doInBackground() */
	    protected void onPostExecute(PastyResponse result) {
	       if(result.hasException) {
	    	   handleException(result.getException());
	       } else {
	   		final AddItemFragmentCallbackListener activity = (AddItemFragmentCallbackListener) getSherlockActivity();
	    	int duration = Toast.LENGTH_LONG;
	   		CharSequence text = getString(R.string.item_added);
	   		Toast toast = Toast.makeText(context, text, duration);
	   		toast.show();
	   		toast = null;
	   		context = null;
	   		activity.onAddItemFragmentCallbackSignal(PastySharedStatics.SIGNAL_EXIT);
	       }
	    }
	}
	
	 protected void handleException(PastyException mException) {
	    	TextView mHelpTextBig = (TextView) getSherlockActivity().findViewById(R.id.tvHelpTextBig);
	    	TextView mHelpTextSmall = (TextView) getSherlockActivity().findViewById(R.id.tvHelpTextSmall);
	    	switch(mException.errorId) {
 		case PastyException.ERROR_AUTHORIZATION_FAILED:
 			Log.d(TAG, "ERROR_AUTHORIZATION_FAILED EXCEPTION");
				mHelpTextBig.setTextColor(getResources().getColor(R.color.white));
				mHelpTextBig.setBackgroundColor(getResources().getColor(R.color.red));
				mHelpTextBig.setText(R.string.error_login_failed_title);
				mHelpTextSmall.setText(R.string.error_login_failed);
				return;
			case PastyException.ERROR_IO_EXCEPTION:
 			Log.d(TAG, "ERROR_IO_EXCEPTION");
				mHelpTextBig.setTextColor(getResources().getColor(R.color.white));
				mHelpTextBig.setBackgroundColor(getResources().getColor(R.color.red));
				mHelpTextBig.setText(R.string.error_io_title);
				mHelpTextSmall.setText(R.string.error_io);
			case PastyException.ERROR_ILLEGAL_RESPONSE:
				Log.d(TAG, "ERROR_ILLEGAL_RESPONSE EXCEPTION");
				mHelpTextBig.setTextColor(getResources().getColor(R.color.white));
				mHelpTextBig.setBackgroundColor(getResources().getColor(R.color.red));
				mHelpTextBig.setText(R.string.error_badanswer_title);
				mHelpTextSmall.setText(R.string.error_badanswer);
				return;
			case PastyException.ERROR_UNKNOWN:
				mHelpTextBig.setTextColor(getResources().getColor(R.color.white));
				mHelpTextBig.setBackgroundColor(getResources().getColor(R.color.red));
				mHelpTextBig.setText(R.string.error_unknown_title);
				mHelpTextSmall.setText(R.string.error_unknown);
				return;
			default:
				break;
			}

			mHelpTextBig = null;
	    }
	
}
