package hk.ust.comp4521.instant_messaging.server_utils;

import java.lang.ref.WeakReference;
import java.util.Random;

import android.app.Application;
import android.content.Context;
import android.util.Log;

// Cant create anonymous class since it will pass the value into constructor by reference
abstract public class RequestTask {
		
	
	private static final String TAG = "HttpRequestHandler";
	
	protected static final int MAX_ATTEMPTS = 10;
	protected static final int SLOT_TIME_NANOSECOND = 51200;
	protected static final Random RANDOM = new Random();
	
	public enum RequestStatus{
		SUCCESS,
		ERROR,
		WAIT_FOR_NETWORK,
		STOPPED;
	}
	
	public enum ActionForError{
		FORCE_STOP,
		RETRY_WHEN_NEXT_CONNECTION,
		RETRY_EXPONENTIALLY;
	}
	
	protected RequestTaskManager requestTaskManager;
	
	protected long id;
	protected HttpRequest httpRequest;
	protected Context applicationContext;
	protected ActionForError actionForError;
	protected WeakReference<OnHandleResultWithHostListener> hostComponent;
	protected OnHandleResultWithoutHostListener mListenerWithoutHost;
	
	protected RequestTask(Context applicationContext, long id, HttpRequest httpRequest, ActionForError actionForError, OnHandleResultWithHostListener mListenerWithHost, OnHandleResultWithoutHostListener mListenerWithoutHost){
		hostComponent = new WeakReference<OnHandleResultWithHostListener>(mListenerWithHost);
		this.applicationContext = applicationContext;
		this.id = id;
		this.httpRequest = httpRequest;
		this.actionForError = actionForError;
		requestTaskManager = RequestTaskManager.getInstance();
		requestTaskManager.addNewTask(this);
		
		if(mListenerWithoutHost != null){
			if(mListenerWithoutHost.getClass().getEnclosingClass() == null)
				this.mListenerWithoutHost = mListenerWithoutHost;
			else
				Log.e(TAG,"Supplied OnHandleResultWithoutHostListener class should not be declared as inner class or anonymous class. Therefore the all function provided by mListenerWithoutHost is blocked");
		}
	}
	
	public long getId(){
		return id;
	}
	/*
	public String getName(){
		return taskName;
	}
	*/
	
	abstract public void initializeTask();
	
	abstract public void stop();
	abstract public void start();
	
}