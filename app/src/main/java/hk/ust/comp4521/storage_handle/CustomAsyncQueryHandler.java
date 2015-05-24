package hk.ust.comp4521.storage_handle;

import java.util.HashMap;
import java.util.Map;

import hk.ust.comp4521.Common;
import hk.ust.comp4521.instant_messaging.custom_request.BulkSendTask;
import hk.ust.comp4521.instant_messaging.custom_request.OnHandleBulkMessageSendWithoutHostListener;
import hk.ust.comp4521.instant_messaging.server_utils.OnHandleResultWithHostListener;
import hk.ust.comp4521.instant_messaging.server_utils.OneTimeTask;
import hk.ust.comp4521.instant_messaging.server_utils.PostRequest;
import hk.ust.comp4521.instant_messaging.server_utils.RequestId;
import hk.ust.comp4521.instant_messaging.server_utils.ServerUtils;
import hk.ust.comp4521.instant_messaging.server_utils.RequestTask.ActionForError;
import android.app.Application;
import android.content.AsyncQueryHandler;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class CustomAsyncQueryHandler extends AsyncQueryHandler {
	
	private static final String TAG = "CustomAsyncQueryHandler";

	private static final int TOKEN_NONE = 0;
	private Context applicationContext;
	
	public CustomAsyncQueryHandler(Context applicationContext) {
		super(applicationContext.getContentResolver());
		this.applicationContext = applicationContext;
	}
	
	public void startInsert(Uri uri, ContentValues initialValues){
		startInsert(TOKEN_NONE, null, uri, initialValues);
	}

	public void startDelete(Uri uri, String selection, String[] selectionArgs){
		startDelete(TOKEN_NONE, null, uri, selection, selectionArgs);
	}
	
	public void  startQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy){
		startQuery(TOKEN_NONE, null, uri, projection, selection, selectionArgs, orderBy);
	}
	
	public void startUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs){
		startUpdate(TOKEN_NONE, null, uri, values, selection, selectionArgs);
	}
	
	@Override
	protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
		int msgCount = cursor.getCount();
		Log.i("Number Of pending task", String.valueOf(msgCount));
		if(msgCount > 0){
			Long[] msgIds = new Long[msgCount];
			String[] msgs = new String[msgCount];
			String[] tos = new String[msgCount];
			int i = 0;
			
			int msgIdIndex = cursor.getColumnIndex(DataProvider.COL_ID);
			int msgIndex = cursor.getColumnIndex(DataProvider.MESSAGES_COL_MSG);
			int toIndex = cursor.getColumnIndex(DataProvider.MESSAGES_COL_TO);
			while(cursor.moveToNext()){
				msgIds[i] =  cursor.getLong(msgIdIndex);
				msgs[i] = cursor.getString(msgIndex);
				tos[i] = cursor.getString(toIndex);
				i++;
			}
			cursor.close();
	
			BulkSendTask task = new BulkSendTask(applicationContext, msgIds, msgs, tos);
			task.start();
			
		}
	}
	
	
}
