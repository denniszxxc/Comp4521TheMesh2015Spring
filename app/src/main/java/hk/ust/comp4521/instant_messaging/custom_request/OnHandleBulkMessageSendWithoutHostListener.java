package hk.ust.comp4521.instant_messaging.custom_request;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import hk.ust.comp4521.instant_messaging.server_utils.OnHandleResultWithoutHostListener;
import hk.ust.comp4521.instant_messaging.server_utils.RequestId;
import hk.ust.comp4521.storage_handle.DataProvider;



public class OnHandleBulkMessageSendWithoutHostListener extends OnHandleResultWithoutHostListener{

	private ContentResolver cr;
	//private String[] targetUsername;
	private String inClause;
	
	public OnHandleBulkMessageSendWithoutHostListener(ContentResolver cr, String[] targetUsername){
		this.cr = cr;
		//this.targetUsername = targetUsername;
		/*
		int targetUsernameSize = targetUsername.length;
		this.targetUsername = new String[targetUsernameSize];
		for(int i = 0; i < targetUsernameSize; i++){
		    this.targetUsername[i] = String.valueOf(targetUsername[i]);
		}
		*/
		
		boolean hasComma = false;
		for(int i = 0; i < targetUsername.length; i++){
		    if(hasComma)
		    	inClause = inClause+","+targetUsername[i];
	    	else{
	    		hasComma = true;
	    		inClause = inClause+targetUsername[i];
	    	}
		}
		
	}
	
	@Override
	public void onResponse(long id, String response) {

		ContentValues values = new ContentValues(1);
		values.put(DataProvider.MESSAGES_COL_SENDED, 1);
		cr.update(DataProvider.CONTENT_URI_MESSAGES, values, DataProvider.COL_ID+"=?", new String[]{inClause});

		
	}
	
	
}
