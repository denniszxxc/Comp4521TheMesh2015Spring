package com.comp4521.bookscan;

import com.example.bookscan.R;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

@SuppressWarnings("deprecation")
public class CameraManager {
	private Camera mCamera;
	private static Context mContext;
	
	public CameraManager(Context context) {// if want camera, new this
		mContext = context;
		// Create an instance of Camera
        mCamera = getCameraInstance();
	}

	private static boolean checkCameraHardware() {
	    if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        return true; // this device has a camera
	    } else {   
	        return false; // no camera on this device
	    }
	}
	
	public Camera getCamera() {
		return mCamera;
	}

	/*
	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}
	
	public void onPause() {
		releaseCamera();
	}
	
	public void onResume() {
		if (mCamera == null) {
			mCamera = getCameraInstance();
		}
		
		Toast.makeText(mContext, "preview size = " + mCamera.getParameters().getPreviewSize().width + 
				", " + mCamera.getParameters().getPreviewSize().height, Toast.LENGTH_LONG).show(); 
	}*/
	
	/** A safe way to get an instance of the Camera object. */
	private static Camera getCameraInstance(){
	    Camera c = null;
	    if(checkCameraHardware()) {
	    	try {
	    		c = Camera.open(); // attempt to get a Camera instance
	    	}
	    	catch (Exception e){
	    		// Camera is not available (in use or does not exist)
	    		Log.e(mContext.getString(R.string.app_name), "failed to open Camera");
	    		e.printStackTrace();
	    	}
	    }
	    return c; // returns null if camera is unavailable
	}
}