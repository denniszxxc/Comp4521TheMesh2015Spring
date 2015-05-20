package com.comp4521.bookscan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

@SuppressWarnings("deprecation")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "CameraPreview";
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private Context mContext;
	List<Size> mSupportedPreviewSizes;
	private MultiFormatReader mMultiFormatReader;
	private int prevWidth, prevHeight;
    private int mLeft, mTop, mAreaWidth, mAreaHeight; // init by setArea(...)
    private boolean isAutoFocusing = false;
    private boolean isPreviewing = false;
    private boolean isPreviewCallback = false;
    private AutoFocusTimerTask mAutoFocusTimerTask;
    private PreviewCallbackTimerTask mPreviewCallbackTimerTask;
    private Timer mAutoFocusTimer;
    private Timer mPreviewCallbackTimer;
    private GetBookInfo bookInfo = null;
    private Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
    private ScannerSound mSound;
	private AlertDialog mDialog;
	private boolean mIsPortait;
    private AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback() {
    	@Override
        public void onAutoFocus(boolean success, Camera camera) {
            // TODO Auto-generated method stub
            if(success) { // success mean focus success
                Log.i(TAG, "myAutoFocusCallback: success...");
            }
            else { // focus not success yet
                Log.i(TAG, "myAutoFocusCallback: fail..."); 
            }
        }
    };
    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {        
			// TODO Auto-generated method stub
			if (mDialog.isShowing())
				return;
	        LuminanceSource source = new PlanarYUVLuminanceSource(data, prevWidth, prevHeight, mLeft, mTop,
	        		mAreaWidth, mAreaHeight, mIsPortait); // where mLeft + mAreaWidth <= prevWidth && mTop + mAreaHeight <= prevHeight
	        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(
	        		source));
	        Result result;
	        try {
	        	result = mMultiFormatReader.decode(bitmap, hints);
	        	if(!bookInfo.getDialogState())
	        		mSound.playSound();
	            if(bookInfo.runGetBookDataTask(result.getText().toString())) {;
		    		mDialog.setMessage("Scan Success!");
		    		mDialog.setCanceledOnTouchOutside(true);
		    		mDialog.show();
	            }
	        } catch (NotFoundException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
		}
    };
	
    public CameraPreview(Context context, Camera camera, boolean isPortait) {
    	super(context);
    	mContext = context;
    	mIsPortait = isPortait;
    	setCamera(camera);
    	setHolder();
		mSound = new ScannerSound(mContext);
	    bookInfo = new GetBookInfo(mContext);
	    mDialog = new AlertDialog.Builder(mContext).create();
		setHints();
	}

    public void stopPreviewAndFreeCamera() {
		getHolder().removeCallback(this);
        if (mCamera != null) {
        	surfaceDestroyed(mHolder); 
        	Log.i("CameraPreview.stopPreviewAndFreeCamera()", "mCamera.stopPreview()");
            mCamera.release();
            Log.i("CameraPreview.stopPreviewAndFreeCamera()", "mCamera.release()");
            mCamera = null;
        }
    }
    
    public void setBookResult(ArrayList<String> bookResult) {
    	if(bookInfo != null)
    		bookInfo.setBookResult(bookResult);
    }
    
    public ArrayList<String> getBookResult() {
    	if(bookInfo != null)
    		return bookInfo.getBookResult();
    	else
    		return new ArrayList<String>();
    }
    
    public void setAreaLandscape(int left, int top, int areaWidth, int width) {
    	double ratio = width / prevWidth;
    	mLeft = (int) (left / (ratio + 1));
    	mTop = (int) (top / (ratio + 1));
    	mAreaHeight = mAreaWidth = prevWidth - mLeft * 2;
    }
    
    public void setAreaPortrait(int left, int top, int areaWidth, int height) {
    	double ratio = height / prevHeight;
    	mLeft = (int) (left / (ratio + 1));
    	mTop = (int) (top / (ratio + 1));
    	mAreaHeight = mAreaWidth = prevHeight - mTop * 2;
    }
    
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	    try {
	    	Log.d(TAG, "surfaceCreated!");
	        mCamera.setPreviewDisplay(holder);
	        mCamera.startPreview();
	        isPreviewing = true;
	        setTimers();
	    } catch (IOException e) {
	        Log.d(TAG, "Error setting camera preview: " + e.getMessage());
	    }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	    try {
	    	Log.d(TAG, "surfaceDestroyed!");
	    	endAutoFocusTimer();
	    	bookInfo.stopGetBookDataTaskOperation();
	    	endPreviewCallbackTimer();
	        mCamera.stopPreview();
	        isPreviewing = false;
	    } catch (Exception e){
	        Log.d(TAG, "Error stop camera preview: " + e.getMessage());
	    }
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	    if (mHolder.getSurface() == null) {
	        return;
	    }
	    Log.d(TAG, "surfaceChanged!");
	    surfaceDestroyed(mHolder);
	    try {
	    } catch (Exception e) {
	        Log.d(TAG, "Error starting camera preview: " + e.getMessage());
	    }
        surfaceCreated(mHolder);
	}
	
	private void setCamera(Camera camera) {
		if(camera != mCamera)
			mCamera = camera;
		if(mIsPortait)
			mCamera.setDisplayOrientation(90);
	    mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
		Parameters params = mCamera.getParameters();
		//prevWidth = mSupportedPreviewSizes.get(6).width; // supported preview size which mean frame size capture from camera
		//prevHeight = mSupportedPreviewSizes.get(6).height; // supported preview size which mean frame size capture from camera
		prevWidth = 640;
		prevHeight = 480;
	    Log.i(TAG, "prevWidth(x): " + Integer.toString(prevWidth));
		Log.i(TAG, "prevHeight(y): " + Integer.toString(prevHeight));
	    params.setPreviewSize(prevWidth, prevHeight);
	    params.setFocusMode("auto"); // assume device have auto foucs
	    mCamera.setParameters(params);
	    mMultiFormatReader = new MultiFormatReader();
    }
    
	private void setHolder() {
	    mHolder = getHolder();
	    mHolder.addCallback(this);
	    mHolder.setKeepScreenOn(true);
	    mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	private void setHints() {
		Vector<BarcodeFormat> formats = new Vector<BarcodeFormat>();
		formats.add(BarcodeFormat.EAN_8);
		formats.add(BarcodeFormat.EAN_13);
		formats.add(BarcodeFormat.QR_CODE);
		hints.put(DecodeHintType.POSSIBLE_FORMATS, formats);
	}
	
	private void setTimers() {
        mAutoFocusTimer = new Timer();
        mAutoFocusTimerTask = new AutoFocusTimerTask();
	    mAutoFocusTimer.schedule(mAutoFocusTimerTask, 200, 2500); // to make auto focus to do regularly
	    mPreviewCallbackTimer = new Timer();
	    mPreviewCallbackTimerTask = new PreviewCallbackTimerTask();
	    mPreviewCallbackTimer.schedule(mPreviewCallbackTimerTask, 100, 300); // to make Preview Callback  to do regularly
	}
	
    private class AutoFocusTimerTask extends TimerTask {  
        @Override  
        public void run() {  
            // TODO Auto-generated method stub  
            if(mCamera != null && isPreviewing && !isAutoFocusing) {  
            	isAutoFocusing = true;
            	try{
            		mCamera.autoFocus(mAutoFocusCallback);
            	} catch(Exception e) {
                	Log.i(TAG, "AutoFocus fail! " + e.getMessage());
                }
                isAutoFocusing = false;
            }
        }
    }
    
    private class PreviewCallbackTimerTask extends TimerTask {
        @Override  
        public void run() {  
            // TODO Auto-generated method stub
        	if(mCamera != null && !isPreviewCallback) {
        		isPreviewCallback = true;
        		try{
            		mCamera.setOneShotPreviewCallback(mPreviewCallback);
            	} catch(Exception e) {
                	Log.i(TAG, "PreviewCallback fail! " + e.getMessage());
                }
        	}
        	isPreviewCallback = false;
        }
    }
    
    private void endAutoFocusTimer() {
    	mAutoFocusTimer.cancel();
    	mAutoFocusTimer = null;
    	mAutoFocusTimerTask.cancel();
    	mAutoFocusTimerTask = null;
    	mCamera.cancelAutoFocus();
    	isAutoFocusing = false;
    }
    
    private void endPreviewCallbackTimer() {
    	mPreviewCallbackTimer.cancel();
    	mPreviewCallbackTimer = null;
    	mPreviewCallbackTimerTask.cancel();
    	mPreviewCallbackTimerTask = null;
    	mCamera.setPreviewCallback(null);
    	isPreviewCallback = false;
    }
}