package hk.ust.comp4521.instant_messaging.custom_request;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;
import hk.ust.comp4521.instant_messaging.server_utils.OnHandleResultWithoutHostListener;
import hk.ust.comp4521.instant_messaging.server_utils.RequestId;
import hk.ust.comp4521.storage_handle.DataProvider;



public class OnHandleMessageSendWithoutHostListener extends OnHandleResultWithoutHostListener{

	private ContentResolver cr;
	
	public OnHandleMessageSendWithoutHostListener(ContentResolver cr){
		this.cr = cr;
	}
	
	@Override
	public void onResponse(long id, String response) {
		JSONObject reader;
		try {
			reader = new JSONObject(response);
			if(reader.getBoolean("success")){
				ContentValues values = new ContentValues(1);
				values.put(DataProvider.MESSAGES_COL_SENDED, 1);
				cr.update(Uri.withAppendedPath(DataProvider.CONTENT_URI_MESSAGES, String.valueOf(id)), values, null, null);
				Log.i("Change Sended state","Success");
			}
		} catch (JSONException e) {}
		

		
	}
	
	
}
