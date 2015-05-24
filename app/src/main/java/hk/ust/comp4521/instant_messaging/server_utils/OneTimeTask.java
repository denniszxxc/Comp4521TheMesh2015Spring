package hk.ust.comp4521.instant_messaging.server_utils;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public final class OneTimeTask extends RequestTask{
	
	private final String TAG;
	public AsyncTask<Void, Void, RequestStatus> task;
	
	public OneTimeTask(Context applicationContext, long id, HttpRequest httpRequest, ActionForError actionForError, OnHandleResultWithHostListener mListenerWithHost, OnHandleResultWithoutHostListener mListenerWithoutHost) {
		super(applicationContext, id, httpRequest, actionForError, mListenerWithHost, mListenerWithoutHost);
		TAG = "Task "+id;
	}
	

	@Override
	public void start() {
		stop();
		initializeTask();
		task.execute();
	}
	
	
	@Override
	public void stop() {
		if(task != null)
			task.cancel(true);			
	}
	
	@Override
	public void initializeTask(){
		
		task = new AsyncTask<Void, Void, RequestStatus>(){
			
			private String response;
			
			@Override
			protected RequestStatus doInBackground(Void... params) {
				Log.i(TAG,"Start doInBackground");
				
				RequestStatus status = RequestStatus.ERROR;
				
				boolean breakLoop = false;
				
				for (int i = 1; i <= MAX_ATTEMPTS; i++) {
					if(breakLoop)
						break;
					
		    		Log.d(TAG, "Attempt #" + i + " to connect");
		    		
	    			try {    		
	    				response = httpRequest.request();
	    				status = RequestStatus.SUCCESS;
	    				break;
		    		} 
		    		catch (IllegalArgumentException iae) { // Invalid URL
		    			Log.e("IllegalArgumentException in posting", iae.getMessage());
		    			break;
		    		}
		    		catch (Exception e) { // some error when construct or send request or parse result so the server may be down
		    			// error may be caused by network connection
		    			if(ServerUtils.isOnline(applicationContext)){
		    				Log.e("Test","isOnline");
		    				switch(actionForError){    			
			    			case FORCE_STOP:
			    				status = RequestStatus.STOPPED;
			    				breakLoop = true;
			    				break;
			    			case RETRY_WHEN_NEXT_CONNECTION:
			    				requestTaskManager.pendNetwork(applicationContext, id);
			            		status = RequestStatus.WAIT_FOR_NETWORK;
			            		breakLoop = true;
			    				break;
			    			case RETRY_EXPONENTIALLY:
			    				if(i != MAX_ATTEMPTS){								
					                try {
					                	long backoff = SLOT_TIME_NANOSECOND * RANDOM.nextInt((int)Math.pow(2,i)); 
					                	long backoffMilli = backoff/1000;
					                	int backoffNano = (int)(backoff%1000);
					                    Thread.sleep(backoffMilli,backoffNano);
					                } 
					                catch (InterruptedException ie) {
					                    Thread.currentThread().interrupt();
					                    status = RequestStatus.STOPPED; 
					                    breakLoop = true; // thread is interrupted
					                }
			    				}
			    				break;		    			
			    			}    				
		    			}
		    			else{
		    				Log.e("Test","isOffline");
		    				requestTaskManager.pendNetwork(applicationContext, id);
		            		status = RequestStatus.WAIT_FOR_NETWORK;
		            		breakLoop = true;
		    			}		    			
		            }
	    		}
		    	if(status != RequestStatus.WAIT_FOR_NETWORK)
		    		requestTaskManager.removeTask(id);
				Log.i(TAG,"End doInBackground");
				return status;
			}
			
			@Override
			protected void onPostExecute(RequestStatus status) {
				boolean hasntHost;
				OnHandleResultWithHostListener mListenerWithHost = hostComponent.get();
				if(hostComponent.get() == null)
					hasntHost = false;
				else 
					hasntHost = true;
				
				switch(status){
					case SUCCESS:
						if(hasntHost)
							mListenerWithHost.onResponse(id, response);
						else if(mListenerWithoutHost != null)
							mListenerWithoutHost.onResponse(id, response);
						break;
					case ERROR:
						if(hasntHost)
							mListenerWithHost.onError(id);
						else if(mListenerWithoutHost != null)
							mListenerWithoutHost.onError(id);
						break;
					case STOPPED:
						if(hasntHost)
							mListenerWithHost.onStop(id);
						else if(mListenerWithoutHost != null)
							mListenerWithoutHost.onStop(id);
					case WAIT_FOR_NETWORK:
						if(hasntHost)
							mListenerWithHost.onWaitingNetwork(id);
						else if(mListenerWithoutHost != null)
							mListenerWithoutHost.onWaitingNetwork(id);
						break;
				}			
			}
			
		};
	}
}
