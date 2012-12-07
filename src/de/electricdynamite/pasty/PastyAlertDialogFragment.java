package de.electricdynamite.pasty;

/*
 *    Copyright 2012 Philipp Geschke
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


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;


public class PastyAlertDialogFragment extends SherlockDialogFragment {
	
	protected int id;
	
	public interface PastyAlertDialogListener {
        void onFinishPastyAlertDialog(int signal);
    }

	public PastyAlertDialogFragment() {
    }
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if(savedInstanceState != null) {
			this.id = savedInstanceState.getInt("id");
		}
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
        		})
    			.setNegativeButton(R.string.button_exit, new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    			    activity.onFinishPastyAlertDialog(PastySharedStatics.SIGNAL_EXIT);
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
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    outState.putInt("id", this.id);
	    super.onSaveInstanceState(outState);
	}
	
	public void setId(int id) {
		this.id = id;
	}
}

