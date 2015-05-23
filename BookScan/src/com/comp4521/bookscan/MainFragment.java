package com.comp4521.bookscan;

import com.example.bookscan.R;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements OnClickListener {
	private ImageButton scanBtn;
	
    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.test_main, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        scanBtn = (ImageButton)rootView.findViewById(R.id.scan_button);
        scanBtn.setOnClickListener(this);
        return rootView;
    }

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
        	case R.id.scan_button:
        	Intent intent = new Intent(getActivity(), BookScanActivity.class);
        	intent.putExtra("type", "lend"); // can be "donate"
        	startActivity(intent);        	
        	break;
        }
	}
}
