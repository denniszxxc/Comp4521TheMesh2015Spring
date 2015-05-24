
package hk.ust.comp4521.instant_messaging.gcm;

import java.io.IOException;

import hk.ust.comp4521.Common;
import hk.ust.comp4521.PlayServiceCantSolvedAlert;
import hk.ust.comp4521.instant_messaging.server_utils.ServerUtils;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMUtils {
	
	public static final int AFTER_SOLVE_PLAY_SERVICES_REQUEST = 9000;
	private boolean allowRegister;
	private Activity mActivity;
	private GoogleCloudMessaging gcm;
	private OnHandleResultListener mListener;
	private boolean isRegistered;
	
	private static final String TAG = "GCMUtils";
	private String regId;
	
	private int currentVersion;
	
	public interface OnHandleResultListener{
		public void onSuccess(String regId);
		public void onError();
	}
	
	public void registeredToServer(){
		isRegistered = true;
		isRegistered = Common.saveExistedRegIdIsRegistered(true);		
	}
	
	public GCMUtils(Activity mActivity){
		allowRegister = false;
		this.mActivity = mActivity;
		gcm = GoogleCloudMessaging.getInstance(mActivity);
		// if the host activity has not OnHandleResultListener interface, the application will throw error.
		// It is because this class will be useless without this interface
		mListener = (OnHandleResultListener)mActivity;
		regId = Common.getRegId();
		if (!regId.equals("")) {
        	int versionInRegistration = Common.getAppVersion();
            currentVersion = getAppVersion();
            if (versionInRegistration != currentVersion) {
            	 Log.i(TAG,"The version in registration is not equal to the current version");
            	 regId = "";
            }
        }
		if(regId.equals(""))
			isRegistered = false;
		else
			isRegistered = Common.isExistedRegIdIsRegistered();
		
	}
	
	public void checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity.getApplication());
        if (resultCode == ConnectionResult.SUCCESS)
        	allowRegister = true;
        else {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode, mActivity, AFTER_SOLVE_PLAY_SERVICES_REQUEST, new DialogInterface.OnCancelListener(){

					@Override
					public void onCancel(DialogInterface dialog) {
						mActivity.finish();						
					}
					
                }).show();
            }
            else
            	new PlayServiceCantSolvedAlert().show(mActivity.getFragmentManager(), null);
        }

    }
	
	public void register(){
		if(!allowRegister){
			Log.e(TAG, "Play service is not check before or There is a error in Play service");
			return;
		}
		
		if(isRegistered){
			Log.i(TAG, "The current regId is already registered to server");
			return;
		}
		
		Log.i("regId",String.valueOf(regId));

		if(regId.equals("")){		
			new AsyncTask<Void,Void,Boolean>(){
				
				@Override
				protected Boolean doInBackground(Void... params) {
					try {
						regId = gcm.register(ServerUtils.PROJECT_ID);
						if(Common.saveExistedRegIdIsRegistered(false)){
							if(currentVersion == Integer.MIN_VALUE){
								if(Common.saveAppVersion(-1))
									Common.saveRegId(regId);
							}												
							else{
								if(Common.saveAppVersion(currentVersion))
									Common.saveRegId(regId);
							}
	
						}
						return true;
					} catch (IOException e) {
						return false;
					}
				}
				
				@Override
				protected void onPostExecute(Boolean result) {
					if(result == true)
						mListener.onSuccess(regId);
					else 
						mListener.onError();
				}
			}.execute(new Void[]{});
		}
		else{
			// gcm_is_registered must = false;
			mListener.onSuccess(regId);
		}
	}
	
	public int getAppVersion() {
        try {
        	Context mContextApplication = mActivity.getApplicationContext();
            PackageInfo packageInfo = mContextApplication.getPackageManager().getPackageInfo(mContextApplication.getPackageName(), 0);
            return packageInfo.versionCode;
        } 
        catch (NameNotFoundException e) {
            // force the application to re-register  
        	return Integer.MIN_VALUE;
        }
    }
	
	public void successfullyRegistered(){
		isRegistered = true;
		// gcm_is_registered must = false;
		// no need to worry the value is successfully registered since the registration is already existed in server
		// for next access to this application, it only will update the same reg id to server
		// nothing will be affected except that the application need to waste some resource to do the one-time useless registration 
		Common.saveExistedRegIdIsRegistered(true);		
	}
		
}
