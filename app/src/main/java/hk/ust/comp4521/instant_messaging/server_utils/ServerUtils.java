package hk.ust.comp4521.instant_messaging.server_utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class ServerUtils {
	
	private static final String TAG = "ServerUtilities";

	public static final String SERVER_URL = "http://wwwfyp.cse.ust.hk:7133/instant_messaging";
	public static final String PROJECT_ID = "1044756840055"; // mtreckelvin 
	
	/*
	public static boolean isOnline(Context context){
		Log.i("isOnline","1");
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm != null){
			Log.i("isOnline","2");
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if(info != null){
				Log.i("isOnline","3");
				for(int i = 0; i < info.length; i++){
					if(info[i].getState() == NetworkInfo.State.CONNECTED)
						return true;
				}
				Log.i("isOnline","4");
			}
		}
		return false;
	}*/
	
	
    public static boolean isOnline(Context context){
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if(activeNetwork != null && activeNetwork.isConnected()){
			return true;
		}
		else{
			return false;
		}
	}
    
    
    
}
