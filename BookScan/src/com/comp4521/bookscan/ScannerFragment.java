package com.comp4521.bookscan;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.bookscan.R;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;

@SuppressLint("NewApi") 
public class ScannerFragment extends Fragment implements OnClickListener {
	public static final String TAG = "ScannerFragment";
	private Context activityContext;
    private Button finishBtn;
    private CameraManager mManager;
    private CameraPreview mPreview;
    private ScanRangeView mScanRangeView;
    private View rootView;
    public static ObservableScanner os;
    private ScannerObserver so;
	private boolean saveData = false;
	private boolean mIsPortait = false;
	private ArrayList<String> bookResult = new ArrayList<String>();
	private boolean hasSavedInstanceState = false;
	private long totalInput;
	private long success;
	
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ScannerFragment() {
    	Log.i(TAG, "ScannerFragment class!");
        setArguments(new Bundle());
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	Log.i(TAG, "ScannerFragment onCreateView()!");
        rootView = inflater.inflate(R.layout.scan, container, false);
        if(!mIsPortait)
        	getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setAllListener();
        return rootView;
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) 
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            totalInput = savedInstanceState.getLong("bookInfo_totalInput");
            success = savedInstanceState.getLong("bookInfo_success");
            os = new ObservableScanner(totalInput, success);
            so = new ScannerObserver(activityContext, rootView, os);
            os.addObserver(so);
        	os.setTotalInputValue(totalInput);
        	os.setFoundValue(success);
        	bookResult = savedInstanceState.getStringArrayList("bookInfo_bookResult");
        	hasSavedInstanceState = true;
            Log.d(TAG, "ScannerFragment get savedInstanceState()!");        
        }
        Log.i(TAG, "ScannerFragment onActivityCreated()!");
        super.onActivityCreated(savedInstanceState);
    	if(getArguments() != null)
    		saveData = getArguments().getBoolean("saveData");
    	allCameraSetting();
    }
	
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
    		case R.id.finish:
    		Log.i(TAG, "Save the current article selection in case we need to recreate the fragment!");
    		changeFragment();
    		break;
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) 
	@Override
	public void onPause() {
		if(mPreview != null)
			bookResult = mPreview.getBookResult();	
		super.onPause();
		Log.d(TAG, "ScannerFragment onPause()!");
	}
	
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	// Save away the original text, so we still have it if the activity
    	// needs to be killed while paused.
    	savedInstanceState.clear();
    	totalInput = os.getTotalInputValue();
    	success = os.getFoundValue();
    	savedInstanceState.putLong("bookInfo_totalInput", totalInput);
    	savedInstanceState.putLong("bookInfo_success", success);
    	savedInstanceState.putStringArrayList("bookInfo_bookResult", bookResult);
    	super.onSaveInstanceState(savedInstanceState);
    	Log.d(TAG, "ScannerFragment onSaveInstanceState()!");
    }
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) 
	@Override
	public void onDestroyView() {
		Log.i(TAG, "ScannerFragment onDestroyView()!");
		super.onDestroyView();
        releaseCamera();
	}
	
	private void setAllListener() {
		backButtonListener();
        finishBtn = (Button)rootView.findViewById(R.id.finish);
        finishBtn.setOnClickListener(this);
	}
	
	private void backButtonListener() {
    	// need to do some to check if the user click the back button on the device
    	rootView.setFocusableInTouchMode(true);
    	rootView.requestFocus();
    	rootView.setOnKeyListener(new OnKeyListener() {
    		@Override
    		public boolean onKey(View v, int keyCode, KeyEvent event) {
	    		if (event.getAction() == KeyEvent.ACTION_DOWN) {
		    		if (keyCode == KeyEvent.KEYCODE_BACK) {
		    			showBackButtonDialog();
			    	}
			    }
		    	return false;
	    	}
    	});
	}
	
    private void showBackButtonDialog() {
        // Use the Builder class for convenient dialog construction
    	AlertDialog.Builder mBuilder = new AlertDialog.Builder(activityContext);
        mBuilder.setMessage("Are you sure to leave? If Yes, all scanned book data will be gone!");
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	Intent intent = new Intent(getActivity(), MainActivity.class); // the class may be different
            	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            	startActivity(intent);
            }
        });
        mBuilder.setNegativeButton("Cancel", null);
        // Create the AlertDialog object
        Dialog dialog = mBuilder.create();
		if (!dialog.isShowing())
			dialog.show();
    }
	
	private void allCameraSetting() {
    	activityContext = getActivity();
    	mManager = new CameraManager(activityContext);
    	if (mManager.getCamera() != null) {
    		setObserv();
    		setScannerView();
		}
	}
	
    private void setObserv() {
    	if(!hasSavedInstanceState)
	        if(saveData) {
	        	totalInput = getArguments().getLong("bookInfo_totalInput");
	        	success = getArguments().getLong("bookInfo_success");
	            os = new ObservableScanner(totalInput, success);
	            so = new ScannerObserver(activityContext, rootView, os);
	            os.addObserver(so);
	        	os.setTotalInputValue(totalInput);
	        	os.setFoundValue(success);
	        } else {
	        	totalInput = 0;
	        	success = 0;
	        	os = new ObservableScanner(0, 0);
	        	so = new ScannerObserver(activityContext, rootView, os);
	        	os.addObserver(so);
	        }
    }
    
    @SuppressWarnings("deprecation")
    private void setScannerView() {
		mPreview = new CameraPreview(activityContext, mManager.getCamera(), mIsPortait);
		if(!hasSavedInstanceState) {
			if(saveData) {
				bookResult = getArguments().getStringArrayList("bookInfo_bookResult");
				mPreview.setBookResult(bookResult);
	    	}
		} else
			mPreview.setBookResult(bookResult);
		FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.camera_preview);
    	Display display = getActivity().getWindowManager().getDefaultDisplay();	
		mScanRangeView = (ScanRangeView)rootView.findViewById(R.id.scan_range_view);
		mScanRangeView.update(display.getWidth(), display.getHeight(), mIsPortait);
		if(mIsPortait)
			mPreview.setAreaPortrait(mScanRangeView.getScanRangeTop(), mScanRangeView.getScanRangeLeft(), 
					mScanRangeView.getScanRangeAreaWidth(), display.getHeight()); // since ORIENTATION_PORTRAIT
		else
			mPreview.setAreaLandscape(mScanRangeView.getScanRangeLeft(), mScanRangeView.getScanRangeTop(), 
					mScanRangeView.getScanRangeAreaWidth(), display.getWidth()); // since ORIENTATION_LANDSCAPE
		preview.addView(mPreview);
    }
    
    private void changeFragment() {
	    getArguments().putBoolean("saveData", true);
	    getArguments().putLong("bookInfo_totalInput", os.getTotalInputValue());
	    getArguments().putLong("bookInfo_success", os.getFoundValue());
	    getArguments().putStringArrayList("bookInfo_bookResult", mPreview.getBookResult());
		BookListConfirmFragment newFragment = new BookListConfirmFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("bookInfo_bookResult", mPreview.getBookResult());
        newFragment.setArguments(args);
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    
    private void releaseCamera() {
    	if(mPreview != null)
    		mPreview.stopPreviewAndFreeCamera();
    }
}
