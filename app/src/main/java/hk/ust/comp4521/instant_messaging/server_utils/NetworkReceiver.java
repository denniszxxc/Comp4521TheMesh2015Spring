package hk.ust.comp4521.instant_messaging.server_utils;

import java.util.ArrayList;
import java.util.List;

import hk.ust.comp4521.instant_messaging.server_utils.ServerUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver{
	
	public interface NetworkTask {
		public boolean hasTask();
		public void executeTask();
		public void executeOfflineTask();
	}
	
	private static final String TAG = "NetworkReceiver";
	private static final NetworkReceiver networkReceiver = new NetworkReceiver();
	private static final List<NetworkTask> networkTaskList = new ArrayList<NetworkTask>();
	
	private NetworkReceiver(){}
	
	static public NetworkReceiver getInstance(){
		return networkReceiver;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG,"Network change");
		if(ServerUtils.isOnline(context)){
			Log.i(TAG,"Onlined");	
			Log.i(TAG,"Num of task(s): " + networkTaskList.size());
			for(NetworkTask networkTask: networkTaskList){
				networkTask.executeTask();
			}
			carefullyUnregister(context);
		}
		else{
			Log.i(TAG,"Offline");
			for(NetworkTask networkTask: networkTaskList)
				networkTask.executeOfflineTask();
		}
	}	
	
	public void carefullyUnregister(Context context){
		Log.i(TAG,"try to unregister");
		for(NetworkTask networkTask: networkTaskList){
			if(networkTask.hasTask())
				return;
		}
		
		try{
			context.unregisterReceiver(this);
			Log.i(TAG,"Network Receiver is unregistered");
		}
		catch (IllegalArgumentException e){}
	}
	
	public void register(Context context){
		Log.i(TAG,"try to register");
		context.registerReceiver(this,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));		
	}
	
	public NetworkReceiver setNetworkTask(NetworkTask networkTask){
		networkTaskList.add(networkTask);
		return networkReceiver;
	}
}
