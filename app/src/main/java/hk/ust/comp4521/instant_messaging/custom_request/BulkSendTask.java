package hk.ust.comp4521.instant_messaging.custom_request;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import hk.ust.comp4521.Common;
import hk.ust.comp4521.instant_messaging.server_utils.OnHandleResultWithHostListener;
import hk.ust.comp4521.instant_messaging.server_utils.OneTimeTask;
import hk.ust.comp4521.instant_messaging.server_utils.PostRequest;
import hk.ust.comp4521.instant_messaging.server_utils.RequestId;
import hk.ust.comp4521.instant_messaging.server_utils.ServerUtils;
import hk.ust.comp4521.instant_messaging.server_utils.RequestTask.ActionForError;
import hk.ust.comp4521.storage_handle.DataProvider;

public class BulkSendTask {
	
	private Context applicationContext;
	private Long[] msgIds;
	private String[] msgs; 
	private String[] tos;
	private String from;
	private int index;
	private int total;
	
	private static final String TAG = "BulkSendTask";
	
	public BulkSendTask(Context applicationContext, Long[] msgIds, String[] msgs, String[] tos){
		this.applicationContext = applicationContext;
		this.msgIds = msgIds;
		this.msgs = msgs;
		this.tos = tos;
		from = Common.getSelfUid();
		index = 0;
		total = msgs.length;
	}
	
	
	public void start(){
		if(index == total)
			return;
		
		Map<String,String[]> params = new HashMap<String,String[]>();
		params.put("msg", new String[]{msgs[index]});
		params.put("from_uid", new String[]{Common.getSelfUid()});
		params.put("to_uid", new String[]{tos[index]});
		new OneTimeTask(applicationContext, RequestId.BULK_SEND, new PostRequest(ServerUtils.SERVER_URL+"/send.php", params),ActionForError.RETRY_EXPONENTIALLY,new OnHandleResultWithHostListener(){			

			@Override
			public void onResponse(long id, String response) {

					try {
						JSONObject reader = new JSONObject(response);
						if(reader.getBoolean("success")){
							ContentValues values = new ContentValues(1);
							values.put(DataProvider.MESSAGES_COL_SENDED, 1);
							applicationContext.getContentResolver().update(Uri.withAppendedPath(DataProvider.CONTENT_URI_MESSAGES,String.valueOf(msgIds[index])), values, null, null);
							index++;
							start();
						}
					} catch (JSONException e) {
						Log.e(TAG, "There is an error in the JSON response");
					}
				
				
			}

			@Override
			public void onError(long id) {}

			@Override
			public void onStop(long id) {}

			@Override
			public void onWaitingNetwork(long id) {}
			
		}, new OnHandleBulkMessageSendWithoutHostListener(applicationContext.getContentResolver(),tos)).start();
		
	}
}
