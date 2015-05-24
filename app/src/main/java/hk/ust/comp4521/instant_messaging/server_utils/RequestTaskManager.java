package hk.ust.comp4521.instant_messaging.server_utils;

import java.util.Hashtable;
import java.util.Map;

import android.content.Context;
import android.util.Log;

public class RequestTaskManager {
	
	private static final String TAG = "RequestTaskManager";
	
	private Map<Long,RequestTask> readyTasks;
	private Map<Long,RequestTask> pendingTasks;
	private NetworkReceiver networkReceiver;	
	private static final RequestTaskManager requestTaskManager = new RequestTaskManager();
	
	public static RequestTaskManager getInstance(){
		return requestTaskManager;		
	}
	
	private RequestTaskManager(){
		 readyTasks = new Hashtable<Long,RequestTask>();
		 pendingTasks = new Hashtable<Long,RequestTask>();
		 networkReceiver = NetworkReceiver.getInstance();
	}
	
	public void addNewTask(RequestTask requestTask){
		long id= requestTask.getId();
		if(readyTasks.containsKey(id) || pendingTasks.containsKey(id)){
			Log.e(TAG, "There is already a task with same time. Action is absorbed");
			return;
		}
		readyTasks.put(id, requestTask);
	}
	
	public void removeTask(long id){
		readyTasks.remove(id);
		pendingTasks.remove(id);
	}
	
	public void pendNetwork(Context context, long id){
		pendingTasks.put(id,readyTasks.get(id));
		readyTasks.remove(id);
		networkReceiver.register(context);
	}
	
	public void resumeTaskFromPending(){
		for(Map.Entry<Long,RequestTask> pendingTask: pendingTasks.entrySet()){
			pendingTask.getValue().start();
		}
		readyTasks.putAll(pendingTasks);
		pendingTasks.clear();
	}
		
	public boolean hasPendingTask(){
		return pendingTasks.size() > 0? true: false;
	}
	
}
