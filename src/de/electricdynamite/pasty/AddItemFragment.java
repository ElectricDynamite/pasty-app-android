package de.electricdynamite.pasty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockDialogFragment;


public class AddItemFragment extends SherlockDialogFragment {
	private static final String TAG = AddItemFragment.class.toString();

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
	    return inflater.inflate(R.layout.add_item, container, false);
	}

	public interface AddItemFragmentCallbackListener {
        void onAddItemFragmentCallbackSignal(int signal);
    }
	
}
