package de.electricdynamite.pasty;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;


public class PastyAlertDialogFragment extends SherlockDialogFragment {
	
	protected final int id;
	
	public interface PastyAlertDialogListener {
        void onFinishPastyAlertDialog(int signal);
    }
	
	public PastyAlertDialogFragment(int id) {
		this.id = id;
    }
	
	public Dialog onCreateDialog(int id) {
    	AlertDialog.Builder	mBuilder = null;
		AlertDialog mDialog = null;
		final PastyAlertDialogListener activity = (PastyAlertDialogListener) getSherlockActivity();
        switch(id) {
        case PastySharedStatics.DIALOG_CONNECTION_ERROR_ID:   	
        	mBuilder = new AlertDialog.Builder(getSherlockActivity());
        	mBuilder.setMessage(getString(R.string.error_io))
    			.setTitle(R.string.error_io_title)
        		.setCancelable(false)
        		.setPositiveButton(getString(R.string.button_exit), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        			}
        		});
    			mDialog = mBuilder.create();
    			mDialog.show();
				break;
        case PastySharedStatics.DIALOG_NO_NETWORK:   	
        	mBuilder = new AlertDialog.Builder(getSherlockActivity());
        	mBuilder.setMessage(getString(R.string.error_no_network))
        		.setCancelable(false)
        		.setTitle(R.string.error_no_network_title)
        		.setPositiveButton(getString(R.string.button_exit), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        			    activity.onFinishPastyAlertDialog(PastySharedStatics.SIGNAL_EXIT);
        			}
        		});
			    /*.setNegativeButton("No", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int id) {
			             dialog.cancel();
			        }
			    });*/
    			mDialog = mBuilder.create();
    			mDialog.show();
				break;
        case PastySharedStatics.DIALOG_BAD_ANSWER:
        	mBuilder = new AlertDialog.Builder(getSherlockActivity());
        	mBuilder.setMessage(getString(R.string.error_badanswer))
        		.setCancelable(false)
        		.setTitle(R.string.error_badanswer_title)
        		.setPositiveButton(getString(R.string.button_exit), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        			    activity.onFinishPastyAlertDialog(PastySharedStatics.SIGNAL_EXIT);
        			}
        		});
    			mDialog = mBuilder.create();
    			mDialog.show();
				break;
        case PastySharedStatics.DIALOG_AUTH_ERROR_ID: 
        	mBuilder = new AlertDialog.Builder(getSherlockActivity());  	
        	mBuilder.setMessage(getString(R.string.error_login_failed))
        		.setCancelable(false)
        		.setTitle(R.string.error_login_failed_title)
        		.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        			    activity.onFinishPastyAlertDialog(PastySharedStatics.SIGNAL_ACTIVITY_SETTINGS);
        			}
        		});
				mDialog = mBuilder.create();
				mDialog.show();
				break;
        case PastySharedStatics.DIALOG_CREDENTIALS_NOT_SET: 
        	mBuilder = new AlertDialog.Builder(getSherlockActivity());  	
        	mBuilder.setMessage(getString(R.string.error_credentials_not_set))
        		.setCancelable(true)
        		.setTitle(R.string.error_credentials_not_set_title)
        		.setPositiveButton(R.string.button_get_started, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        			    activity.onFinishPastyAlertDialog(PastySharedStatics.SIGNAL_ACTIVITY_SETTINGS);
        			}
        		});
				mDialog = mBuilder.create();
				mDialog.show();
				break;
        case PastySharedStatics.DIALOG_UNKNOWN_ERROR_ID: 
        	mBuilder = new AlertDialog.Builder(getSherlockActivity());  	
        	mBuilder.setMessage(getString(R.string.error_unknown))
        		.setCancelable(false)
        		.setTitle(R.string.error_unknown_title)
        		.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        			}
        		});
				mDialog = mBuilder.create();
				mDialog.show();
				break;
        case PastySharedStatics.DIALOG_NOT_SUPPORTED_ID: 
        	mBuilder = new AlertDialog.Builder(getSherlockActivity());  	
        	mBuilder.setMessage(getString(R.string.error_not_supported))
        		.setCancelable(false)
        		.setPositiveButton(getString(R.string.button_noes), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        			}
        		});
				mDialog = mBuilder.create();
				mDialog.show();
				break;
        default:
            mDialog = null;
        }
        return mDialog;
    }
}

