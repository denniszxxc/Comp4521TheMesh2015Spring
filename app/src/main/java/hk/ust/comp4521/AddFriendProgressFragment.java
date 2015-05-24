package hk.ust.comp4521;

import hk.ust.comp4521.instant_messaging.server_utils.OnHandleResultWithHostListener;
import hk.ust.comp4521.instant_messaging.server_utils.OneTimeTask;
import hk.ust.comp4521.instant_messaging.server_utils.PostRequest;
import hk.ust.comp4521.instant_messaging.server_utils.RequestId;
import hk.ust.comp4521.instant_messaging.server_utils.RequestTaskManager;
import hk.ust.comp4521.instant_messaging.server_utils.ServerUtils;
import hk.ust.comp4521.instant_messaging.server_utils.RequestTask.ActionForError;
import hk.ust.comp4521.storage_handle.DataProvider;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.bookscan.R;

public class AddFriendProgressFragment extends DialogFragment{

	public interface OnHandleAddedSuccessfullyListener{
		public void onHandleAddedSuccessfully();
	}
	
	//private static final String SELF_UID_TAG = "self_uid";
	private static final String TARGET_UID_TAG = "target_uid";
	private ProgressDialog progressDialog;
	
	private String registering_tag;
	//private String waiting_network_tag;
	
	private OnHandleAddedSuccessfullyListener mListener;
	
	private AddFriendProgressFragment(){}
	
	public static AddFriendProgressFragment newInsatnce(String targetUid){
		AddFriendProgressFragment progressDialogFragment = new AddFriendProgressFragment();
		
		Bundle bundle = new Bundle();
		//bundle.putString(SELF_UID_TAG, selfUid);
		bundle.putString(TARGET_UID_TAG, targetUid);
        progressDialogFragment.setArguments(bundle);
		
		return progressDialogFragment;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{
			mListener = (OnHandleAddedSuccessfullyListener)activity;
		}
		catch(ClassCastException cce){
			mListener = null;
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		registering_tag = getResources().getString(R.string.registering);
		//waiting_network_tag = getResources().getString(R.string.waiting_network);
		
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setIndeterminate(true);
		progressDialog.setCanceledOnTouchOutside(false);
		//progressDialog.setMessage("");
		progressDialog.setMessage(registering_tag);
		return progressDialog;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		String selfUid = Common.getSelfUid();
		//String selfUid = getArguments().getString(SELF_UID_TAG);
		final String targetUid = getArguments().getString(TARGET_UID_TAG);
		
		Map<String,String[]> params = new HashMap<String,String[]>();
		params.put("from_uid", new String[]{selfUid});
		params.put("to_uid", new String[]{targetUid});
		new OneTimeTask(getActivity().getApplicationContext(), RequestId.ADD_FRIEND, new PostRequest(ServerUtils.SERVER_URL+"/add_pair.php", params),ActionForError.RETRY_EXPONENTIALLY,new OnHandleResultWithHostListener(){

			@Override
			public void onResponse(long id, String response) {
				boolean hasError = true;
				try {
					JSONObject reader = new JSONObject(response);
					if(reader.getBoolean("success")){
						ContentValues values = new ContentValues(1);
						values.put(DataProvider.USERS_COL_UID, targetUid);
						getActivity().getContentResolver().insert(DataProvider.CONTENT_URI_USERS, values);
						Toast.makeText(getActivity(), "Successfully added", Toast.LENGTH_SHORT).show();
						hasError = false;
						if(mListener != null)
							mListener.onHandleAddedSuccessfully();
					}

				} catch (JSONException e) {}
				
				if(hasError)
					Toast.makeText(getActivity(), "Not successfully added", Toast.LENGTH_SHORT).show();
				
				dismiss();
			}

			@Override
			public void onError(long id) {
				Toast.makeText(getActivity(), "Not successfully added", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStop(long id) {}

			@Override
			public void onWaitingNetwork(long id) {}
			
		}, null).start();

	}	
	
	@Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        RequestTaskManager.getInstance().removeTask(RequestId.ADD_FRIEND);
    }
}
