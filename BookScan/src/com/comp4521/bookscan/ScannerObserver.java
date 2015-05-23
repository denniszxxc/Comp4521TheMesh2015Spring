package com.comp4521.bookscan;

import java.util.Observable;
import java.util.Observer;

import com.example.bookscan.R;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class ScannerObserver implements Observer {
	private ObservableScanner os = null;
	private View rootView;
	private TextView totalTxt;
	private TextView successTxt;
	
	public ScannerObserver(Context mContext, View rootView, ObservableScanner os) {
	      this.os = os;
	      this.rootView = rootView;
	}
	
	@Override
	public void update(Observable obs, Object data) {
		// TODO Auto-generated method stub
		if(os == obs) {
	        switch ((String) data) {
	        	case "bookInfo_totalInput":
	        		totalTxt = (TextView)rootView.findViewById(R.id.scan_total);
	        		totalTxt.setText("Scanned: " + os.getTotalInputValue());
	        		break;
	        	case "bookInfo_found":
	        		successTxt = (TextView)rootView.findViewById(R.id.scan_success);
					successTxt.setText("Found: " + os.getFoundValue());
					break;
	        }
		}
	}
}