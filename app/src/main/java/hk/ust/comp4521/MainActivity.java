//package hk.ust.comp4521;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.json.JSONObject;
//
//import hk.ust.comp4521.instant_messaging.ChatFragment.OnGetSelfUidListener;
//import hk.ust.comp4521.instant_messaging.ChatMenuFragment;
//import hk.ust.comp4521.instant_messaging.custom_request.OnHandleBulkMessageSendWithoutHostListener;
//import hk.ust.comp4521.instant_messaging.gcm.GCMUtils;
//import hk.ust.comp4521.instant_messaging.gcm.GCMUtils.OnHandleResultListener;
//import hk.ust.comp4521.instant_messaging.server_utils.NetworkReceiver;
//import hk.ust.comp4521.instant_messaging.server_utils.NetworkReceiver.NetworkTask;
//import hk.ust.comp4521.instant_messaging.server_utils.PostRequest;
//import hk.ust.comp4521.instant_messaging.server_utils.RequestTaskManager;
//import hk.ust.comp4521.instant_messaging.server_utils.ServerUtils;
//import hk.ust.comp4521.storage_handle.CustomAsyncQueryHandler;
//import hk.ust.comp4521.storage_handle.DataProvider;
//import android.app.Activity;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//
//import com.example.bookscan.R;
//
//public class MainActivity extends Activity implements OnHandleResultListener, OnGetSelfUidListener{
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case 0:
//			AddFriendProgressFragment.newInsatnce("tim").show(getSupportFragmentManager(), null);
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.add(0, 0, 0, "Add");
//		return true;
//	}
//
//
//
//	private GCMUtils gcmUtils;
//	private String selfUsername;
//	private CustomAsyncQueryHandler customAsyncQueryHandler;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//
//		NetworkReceiver networkReceiver = NetworkReceiver.getInstance();
//        // handle request
//        networkReceiver.setNetworkTask(new NetworkTask(){
//
//        	private RequestTaskManager requestTaskManager;
//
//        	// Runs before the constructor each time you instantiate an object
//        	{
//        		requestTaskManager = RequestTaskManager.getInstance();
//        	}
//
//			@Override
//			public boolean hasTask() {
//				return requestTaskManager.hasPendingTask();
//			}
//
//			@Override
//			public void executeTask() {
//				requestTaskManager.resumeTaskFromPending();
//			}
//
//			@Override
//			public void executeOfflineTask() {}
//
//        });
//
//        //customAsyncQueryHandler.startQuery(DataProvider.CONTENT_URI_MESSAGES, new String[]{DataProvider.MESSAGES_COL_MSG, DataProvider.MESSAGES_COL_FROM, DataProvider.MESSAGES_COL_TO, DataProvider.MESSAGES_COL_AT}, DataProvider.MESSAGES_COL_SENDED+"=?", new String[]{"0"}, null);
//
//
//		selfUsername = getIntent().getStringExtra(RegistrationActivity.Intent_USERNAME);
//		if(selfUsername == null){
//			selfUsername = Common.getSelfUid();
//			gcmUtils = new GCMUtils(this);
//			gcmUtils.checkPlayServices();
//			gcmUtils.register();
//			customAsyncQueryHandler = new CustomAsyncQueryHandler(getApplicationContext());
//			customAsyncQueryHandler.startQuery(DataProvider.CONTENT_URI_MESSAGES, new String[]{DataProvider.COL_ID, DataProvider.MESSAGES_COL_MSG, DataProvider.MESSAGES_COL_TO}, DataProvider.MESSAGES_COL_SENDED+"=?", new String[]{"0"}, null);
//		}
//		else
//			Common.saveSelfUid(selfUsername);
//
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//		Common.setIsInForeground(true);
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//		Common.setIsInForeground(false);
//	}
//
//	@Override
//	public void onSuccess(final String regId) {
//		new AsyncTask<Void,Void,Integer>(){
//
//			private final int SUCCESS = 0;
//			private final int UNKNOW_ERROR = 1;
//
//			@Override
//			protected Integer doInBackground(Void... params) {
//
//				try {
//					Map<String,String[]> postParams = new HashMap<String,String[]>();
//					postParams.put("username",new String[]{selfUsername});
//					postParams.put("regId",new String[]{regId});
//					String response = new PostRequest(ServerUtils.SERVER_URL+"/updateRegistration.php",postParams).request();
//					JSONObject reader = new JSONObject(response);
//	        		if(reader.getBoolean("success"))
//	        			return SUCCESS;
//
//				}
//				catch (Exception e) {}
//				return UNKNOW_ERROR;
//			}
//
//			@Override
//			protected void onPostExecute(Integer result) {
//				switch(result){
//				case SUCCESS:
//					gcmUtils.successfullyRegistered();
//					break;
//				}
//			}
//
//		}.execute(new Void[]{});
//
//	}
//
//	@Override
//	public void onError() {}
//
//	@Override
//	public String onGetSelfUid() {
//		return selfUsername;
//	}
//
//}
