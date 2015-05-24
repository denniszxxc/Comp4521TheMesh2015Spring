package com.comp4521.bookscan;

import com.example.bookscan.R;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentTransaction;
import android.app.*;
import android.util.Log;

public class BookScanActivity extends Activity {
	public static final String TAG = "BookScanActivity";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB) 
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_scan_main);
        Log.i(TAG, "BookScanActivity onCreate()!");

        if (savedInstanceState == null) {
        	FragmentTransaction transaction = getFragmentManager().beginTransaction();
        	transaction.add(R.id.container, new ScannerFragment());
        	transaction.commit();
        }
    }

    @Override
    public void onStop() {
    	Log.i(TAG, "BookScanActivity onStop()!");
    	super.onStop();
    }
}