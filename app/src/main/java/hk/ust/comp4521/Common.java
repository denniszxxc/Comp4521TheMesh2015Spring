package hk.ust.comp4521;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Common extends Application {
	private static SharedPreferences prefs;

	private static final String SELFUID = "self_uid"; 
	private static final String REG_ID = "reg_id"; 
	private static final String APP_VERSION = "app_version";
	private static final String GCM_IS_REGISTERED = "gcm_is_registered";
	
	private static boolean isInForeground = false;
	
	@Override
	public void onCreate() {
		super.onCreate();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	public static void setIsInForeground(boolean value){
		isInForeground = value;
	}
	
	public static boolean isInForeground(){
		return isInForeground;
	}
	
	// --------------------------
	
	public static String getSelfUid(){
		return prefs.getString(SELFUID, "");
	}
	
	public static boolean saveSelfUid(String username){
		return prefs.edit().putString(SELFUID, username).commit();
	}
	
	// --------------------------
	
	public static String getRegId(){
		return prefs.getString(REG_ID, "");
	}
	
	public static boolean saveRegId(String regId){
		return prefs.edit().putString(REG_ID, regId).commit();
	}
	
	// --------------------------
	
	public static int getAppVersion(){
		return prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
	}
	
	public static boolean saveAppVersion(int appVersion){
		return prefs.edit().putInt(APP_VERSION, appVersion).commit();
	}
	
	// --------------------------
	
	public static boolean isExistedRegIdIsRegistered(){
		return prefs.getBoolean(GCM_IS_REGISTERED, false);
	}
	
	public static boolean saveExistedRegIdIsRegistered(boolean isRegistered){
		return prefs.edit().putBoolean(GCM_IS_REGISTERED, isRegistered).commit();
	}
	

}
