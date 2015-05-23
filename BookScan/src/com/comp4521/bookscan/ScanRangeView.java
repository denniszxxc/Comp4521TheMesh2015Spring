package com.comp4521.bookscan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ScanRangeView extends View { // the code in this view will be decoded
	private final String DEBUG_TAG = "ScanRangeView";
	private Paint mPaint;
	private int mLeft, mTop, mRight, mBottom;
	private int screenWidth, screenHeight;
	private boolean mIsPortait;
	
	public ScanRangeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mPaint = new Paint();
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(5);
	}
	
	public void update(int width, int height, boolean isPortait) {
		mIsPortait = isPortait;
		screenWidth = width; // width < height if isPortait, else  width > height
		screenHeight = height;
		int centerX = screenWidth / 2;
		int centerY = screenHeight / 2;
		if(isPortait) {
			mLeft = centerX - centerX/2;
			mTop = centerY - centerY/2;
			mRight = centerX + centerX/2;
			mBottom = centerY + centerY/2;
		} else {
			mLeft = centerX - 34*centerX/64;//37*centerX/64;
			mTop = centerY - 35*centerY/64;
			mRight = centerX + 34*centerX/64;//37*centerX/64;
			mBottom = centerY + 35*centerY/64;
		}
		Log.i(DEBUG_TAG, "screenWidth(x): " + Integer.toString(screenWidth));
		Log.i(DEBUG_TAG, "screenHeight(y): " + Integer.toString(screenHeight));
		Log.i(DEBUG_TAG, "centerX: " + Integer.toString(centerX));
		Log.i(DEBUG_TAG, "centerY: " + Integer.toString(centerY));
		Log.i(DEBUG_TAG, "mLeft(x): " + Integer.toString(mLeft));
		Log.i(DEBUG_TAG, "mRight(x): " + Integer.toString(mRight));
		Log.i(DEBUG_TAG, "mTop(y): " + Integer.toString(mTop));
		Log.i(DEBUG_TAG, "mBottom(y): " + Integer.toString(mBottom));
		invalidate();
	}
	
	public int getScanRangeLeft() {
		return mLeft;
	}
	
	public int getScanRangeTop() {
		return mTop;
	}
	
	public int getScanRangeAreaWidth() {
		return mRight - mLeft;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if(mIsPortait) {
			canvas.drawRect((int)(3*screenWidth/16), (int)((2*screenHeight-screenWidth)/8)-25, 
					(int)(13*screenWidth/16), (int)((6*screenHeight+screenWidth)/8)-25, mPaint);
			canvas.drawRect((int)(3*screenWidth/16), (int)((2*screenHeight-screenWidth)/8)-25, 
					screenWidth/2, (int)((6*screenHeight+screenWidth)/8)-25, mPaint);
		} else {
			canvas.drawRect(mLeft, mTop-25, 
					mRight, mBottom-25, mPaint);
			canvas.drawRect(mLeft, mTop-25, 
					mRight, screenHeight/2-25, mPaint);
		}
	}
}