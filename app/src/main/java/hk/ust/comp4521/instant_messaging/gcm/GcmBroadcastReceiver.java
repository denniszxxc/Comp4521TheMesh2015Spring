package hk.ust.comp4521.instant_messaging.gcm;

import hk.ust.comp4521.Common;
import hk.ust.comp4521.MainActivity;
import com.example.bookscan.R;
import hk.ust.comp4521.instant_messaging.ChatMenuFragment;
import hk.ust.comp4521.storage_handle.DataProvider;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmBroadcastReceiver extends BroadcastReceiver {
	
	private static final String TAG = "GcmBroadcastReceiver";
	private static final int mNotificationId = 1;
	
	private Context ctx;
	private ContentResolver cr;

	@Override
	public void onReceive(Context context, Intent intent) {
	
		ctx = context;
		cr = context.getContentResolver();
		
		PowerManager mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		WakeLock mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		mWakeLock.acquire();
		
		try {
			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
			
			String messageType = gcm.getMessageType(intent);
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				//sendNotification("Send error", false);
				Log.e(TAG, "Special status message indicating that there were errors sending one of the messages.");
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				//sendNotification("Deleted messages on server", false);
				Log.e(TAG, "Special status message indicating that some messages have been discarded because they exceeded the storage limits.");
			} else {
				
				String from = intent.getStringExtra("from_uid");
				String msg = intent.getStringExtra("msg");
				if(msg == null){
					String state = intent.getStringExtra("state");
					if(state.equals("add")){
						Log.i(TAG,"Add friend: "+from);
						
						ContentValues values = new ContentValues(1);
						values.put(DataProvider.USERS_COL_UID, from);
						cr.insert(DataProvider.CONTENT_URI_USERS, values);
					}
					else if(state.equals("remove")){
						Log.i(TAG,"Remove friend: "+from);
						cr.delete(DataProvider.CONTENT_URI_USERS, DataProvider.USERS_COL_UID+"=?", new String[]{from});
					}
				}
				else{	
					
					Log.i(TAG,String.format("Message: %1$s\nFrom: %2$s", msg, from));
					
					ContentValues values = new ContentValues(2);
					values.put(DataProvider.MESSAGES_COL_MSG, msg);
					values.put(DataProvider.MESSAGES_COL_FROM, from);				
					cr.insert(DataProvider.CONTENT_URI_MESSAGES, values);
	
					if (!Common.isInForeground()) 
						sendNotification(from+": "+msg, true);
					
				}
					
			}
			setResultCode(Activity.RESULT_OK);
			
		} 
		finally {
			mWakeLock.release();
		}
	}
	
	private void sendNotification(String msg, boolean launchApp) {
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx).setSmallIcon(R.drawable.ic_launcher).setContentTitle(ctx.getString(R.string.app_name)).setContentText(msg);
		if (launchApp) {
			Intent intent = new Intent(ctx, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent resultPendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(resultPendingIntent);
			mBuilder.setAutoCancel(true);
		}
		
		NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(mNotificationId, mBuilder.build());
	}
}
