package hk.ust.comp4521.instant_messaging;

import hk.ust.comp4521.AddFriendProgressFragment;
import hk.ust.comp4521.R;
import hk.ust.comp4521.RemoveFriendProgressFragment;
import hk.ust.comp4521.instant_messaging.custom_request.OnHandleBulkMessageSendWithoutHostListener;
import hk.ust.comp4521.instant_messaging.custom_request.OnHandleMessageSendWithoutHostListener;
import hk.ust.comp4521.instant_messaging.server_utils.OnHandleResultWithHostListener;
import hk.ust.comp4521.instant_messaging.server_utils.PostRequest;
import hk.ust.comp4521.instant_messaging.server_utils.OneTimeTask;
import hk.ust.comp4521.instant_messaging.server_utils.RequestTask.ActionForError;
import hk.ust.comp4521.instant_messaging.server_utils.ServerUtils;
import hk.ust.comp4521.storage_handle.DataProvider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ChatFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnHandleResultWithHostListener {

	public interface OnGetSelfUidListener{
		public String onGetSelfUid();
	}
	
	private static final String TARGET_DATABASE_ID= "target_database_id";
	private static final String INTENT_TARGET_UID= "target_uid";
	private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	
	//private EditText msgEdit;
	private String targetDatabaseId;
	private String targetUid;
	private String selfUid;
	//private Activity mActivity;
	//private ActionBar mActionBar;
	private ListView mListView;
	
	private SimpleCursorAdapter adapter;
	//private CustomAsyncQueryHandler customAsyncQueryHandler;
	
	private OnHandleMessageSendWithoutHostListener mSendedListener;
	//private FragmentManager fm;
	
	private ChatFragment(){}
	
	public static ChatFragment newInstance(long id){
		ChatFragment chatFragment = new ChatFragment();
		
		Bundle args = new Bundle();
        args.putLong(TARGET_DATABASE_ID, id);
		chatFragment.setArguments(args);
		
		return chatFragment;
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		selfUid = ((OnGetSelfUidListener)activity).onGetSelfUid();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Activity mActivity = getActivity();
		ActionBar mActionBar = mActivity.getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		setHasOptionsMenu(true);
		
		targetDatabaseId = String.valueOf(getArguments().getLong(TARGET_DATABASE_ID));
		
		ContentResolver cr = mActivity.getContentResolver();
		Cursor c = cr.query(Uri.withAppendedPath(DataProvider.CONTENT_URI_USERS, targetDatabaseId), null, null, null, null);
		if (c.moveToFirst()) {
			targetUid = c.getString(c.getColumnIndex(DataProvider.USERS_COL_UID));
			mActionBar.setTitle(targetUid);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.chat_fragment, container, false);
	}
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Bundle args = new Bundle();
		args.putString(INTENT_TARGET_UID, targetUid);
		getLoaderManager().initLoader(0, args, this);
		
		View rootView = getView();
		
		mListView = (ListView) rootView.findViewById(R.id.msg_list);
		
		final  EditText msgEdit = (EditText) rootView.findViewById(R.id.msg_edit);	
		Button sendButton = (Button) rootView.findViewById(R.id.send_btn);
		
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.chat_list_item, null, new String[]{DataProvider.MESSAGES_COL_FROM,DataProvider.MESSAGES_COL_MSG, DataProvider.MESSAGES_COL_AT}, new int[]{R.id.uid, R.id.message, R.id.at}, 0);
		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {			
				String from = cursor.getString(cursor.getColumnIndex(DataProvider.MESSAGES_COL_FROM));
				
				switch(view.getId()) {
				case R.id.uid:			
					TextView fromText = (TextView) view;
					fromText.setText(cursor.getString(columnIndex)+":");
					if (from == null)
						fromText.setVisibility(View.GONE);
					else
						fromText.setVisibility(View.VISIBLE);
					return true;
				case R.id.message:				
					LinearLayout parent = (LinearLayout) view.getParent();
					LinearLayout root = (LinearLayout) parent.getParent();
					if (from == null) {
						root.setGravity(Gravity.RIGHT);
						root.setPadding(50, 10, 10, 10);
						//parent.setBackgroundResource(R.drawable.right);
					} else {
						root.setGravity(Gravity.LEFT);
						root.setPadding(10, 10, 50, 10);
						//parent.setBackgroundResource(R.drawable.left);
					}
					break;
					
				case R.id.at:			
					TextView timeText = (TextView) view;
					timeText.setText(getDisplayTime(cursor.getString(columnIndex)));
					return true;					
				}
				return false;
			}
		});				
		mListView.setAdapter(adapter);
		
		sendButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String msg = msgEdit.getText().toString();
				if (!TextUtils.isEmpty(msg)) {
					send(msg);
					msgEdit.setText(null);
				}		
			}
			
		});
	
	}
	
	//----------------------------------------------------------------------------

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String targetUid = args.getString(INTENT_TARGET_UID);
		CursorLoader loader = null;
		if(targetUid != null)
			loader = new CursorLoader(getActivity(), DataProvider.CONTENT_URI_MESSAGES, null, DataProvider.MESSAGES_COL_TO + "=? or " + DataProvider.MESSAGES_COL_FROM + "=?", new String[]{targetUid, targetUid}, DataProvider.MESSAGES_COL_AT+ " ASC"); 
		return loader;
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {		
		adapter.swapCursor(data);
		mListView.setSelection(adapter.getCount() - 1);
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		ContentValues values = new ContentValues(1);
		values.put(DataProvider.USERS_COL_COUNT, 0);
		getActivity().getContentResolver().update(Uri.withAppendedPath(DataProvider.CONTENT_URI_USERS, targetDatabaseId), values, null, null);
	}
	
	
	//----------------------------------------------------------------------------	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.chat, menu);
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_delete:
			//getActivity().getContentResolver().delete(Uri.withAppendedPath(DataProvider.CONTENT_URI_USERS, targetDatabaseId), null, null);
			RemoveFriendProgressFragment.newInsatnce(targetUid).show(getFragmentManager(), null);
			//getFragmentManager().popBackStack();
			//getActivity().getActionBar().setTitle("ChatRoom");
			//return true;
		case android.R.id.home:
			getFragmentManager().popBackStack();
			getActivity().getActionBar().setTitle("ChatRoom");
			return true;
		}
		return false;
	}

	//----------------------------------------------------------------------------
	
	private void send(final String message) {
		
		Activity mActivity = getActivity();
		//SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT,Locale.getDefault());
		//String time = formatter.format(new Date());
		ContentValues values = new ContentValues(2);
		values.put(DataProvider.MESSAGES_COL_MSG, message);
		values.put(DataProvider.MESSAGES_COL_TO, targetUid);
		Uri targetUri = mActivity.getContentResolver().insert(DataProvider.CONTENT_URI_MESSAGES, values);
		if(targetUri != null){
			Map<String,String[]> params = new HashMap<String,String[]>();
			params.put("msg", new String[]{message});
			params.put("from_uid", new String[]{selfUid});
			params.put("to_uid", new String[]{targetUid});
			if(mSendedListener == null)
				mSendedListener = new OnHandleMessageSendWithoutHostListener(mActivity.getContentResolver());
			//new OneTimeTask(mActivity.getApplicationContext(), Long.parseLong(targetUri.getLastPathSegment()), new PostRequest(ServerUtils.SERVER_URL+"/send.php", params),ActionForError.RETRY_EXPONENTIALLY,this, mSendedListener).start();
			new OneTimeTask(mActivity.getApplicationContext(), Long.parseLong(targetUri.getLastPathSegment()), new PostRequest(ServerUtils.SERVER_URL+"/send.php", params),ActionForError.RETRY_EXPONENTIALLY,this, mSendedListener).start();

		}
		else
			Toast.makeText(mActivity, R.string.error_send, Toast.LENGTH_SHORT).show();
		
	}	
	
	private String getDisplayTime(String datetime) {	
		Calendar cal = Calendar.getInstance();
		int nowYear = cal.get(Calendar.YEAR);
		int nowMonth = cal.get(Calendar.MONTH);
		int nowDate = cal.get(Calendar.DATE);
		try {			
			Date dt = (new SimpleDateFormat(TIME_FORMAT, Locale.getDefault())).parse(datetime);
			cal.setTime(dt);
			if (nowYear == cal.get(Calendar.YEAR) && nowMonth == cal.get(Calendar.MONTH) && nowDate == cal.get(Calendar.DATE))
				return DateFormat.getTimeInstance().format(dt);
			return DateFormat.getDateInstance().format(dt);
		} 
		catch (ParseException e) {
			return datetime;
		}
	}

	@Override
	public void onResponse(long id, String response) {
		if(id > 0)
			mSendedListener.onResponse(id, response);
	}

	@Override
	public void onError(long id) {}

	@Override
	public void onWaitingNetwork(long id) {}
	
	@Override
	public void onStop(long id){}
}
