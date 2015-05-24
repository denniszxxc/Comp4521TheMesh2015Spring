package hk.ust.comp4521;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;




import hk.ust.comp4521.instant_messaging.gcm.GCMUtils;
import hk.ust.comp4521.instant_messaging.gcm.GCMUtils.OnHandleResultListener;
import hk.ust.comp4521.instant_messaging.server_utils.PostRequest;
import hk.ust.comp4521.instant_messaging.server_utils.ServerUtils;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RegistrationActivity extends Activity implements OnHandleResultListener{
	private Button startButton;
	private EditText usernameEditText;
	private ProgressBar progressing;
	private TextView errorTextView;
	private List<String> duplicatedName;
	private String currentUsername;
	private GCMUtils gcmUtils;
	
    public static final String Intent_USERNAME = "username";
    private String duplicate_error_msg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		duplicate_error_msg = getResources().getString(R.string.error_duplicated_username);
		
		setContentView(R.layout.activity_startup_setting);
		usernameEditText = (EditText)findViewById(R.id.username_field);
		startButton = (Button)findViewById(R.id.start_button);
		
		progressing = (ProgressBar)findViewById(R.id.progressing);
		
		errorTextView = (TextView)findViewById(R.id.error_description);
		
		usernameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND && v.length() > 0) {
					register(null);
		            return true;
		        }
				return false;
			}
		});
		usernameEditText.addTextChangedListener(new TextWatcher(){

			// called before typing each char
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			// called when typing each char
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			// called after stop typing for each char
			@Override
			public void afterTextChanged(Editable s) {
				errorTextView.setText(null);
				errorTextView.setVisibility(View.GONE);
				String currentUsername = s.toString();
				int strictCurrentUsernameAmount = currentUsername.trim().length();
				boolean checked = duplicatedName.contains(currentUsername);
				if(strictCurrentUsernameAmount > 0 && !checked){
					usernameEditText.setError(null);
					enableStartButton();
				}
				else if(strictCurrentUsernameAmount==0){ // contain space or empty
					if(s.length() > 0) // contain space
						usernameEditText.setError("Username can't only contain spaces");
					diableStartButton();
				} 
				else if(checked){ // duplicatedName.contains(currentUsername)
					usernameEditText.setError(duplicate_error_msg);
					diableStartButton();
				}

				
			}
			
		});
		
		duplicatedName = new ArrayList<String>();
		
		gcmUtils = new GCMUtils(this);
		gcmUtils.checkPlayServices();		
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case GCMUtils.AFTER_SOLVE_PLAY_SERVICES_REQUEST:
			gcmUtils.checkPlayServices();
			break;
		}
	}
	
	public void diableStartButton(){
		startButton.setTextColor(Color.parseColor("#BEBEBE"));
		startButton.setClickable(false);
	}
	
	public void enableStartButton(){
		startButton.setTextColor(Color.parseColor("#FFFFFF"));
		startButton.setClickable(true);
	}
	
	public void resetState(){
		progressing.setIndeterminate(false);
		usernameEditText.setEnabled(true);
	}
	
	public void register(View v){
		progressing.setIndeterminate(true);
		diableStartButton();
		usernameEditText.setEnabled(false);
		currentUsername = usernameEditText.getText().toString();

		gcmUtils.register();
	}
	
	public void successfullyRegister(){
		gcmUtils.successfullyRegistered();
		Intent intent = new Intent(this,MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.putExtra(Intent_USERNAME, currentUsername);
		startActivity(intent);	
	}



	@Override
	public void onSuccess(final String regId) {
		new AsyncTask<Void,Void,Integer>(){

			private final int SUCCESS = 0;
			private final int DUPLICATED_USERNAME = 1;
			private final int UNKNOW_ERROR = 2;
			
			@Override
			protected Integer doInBackground(Void... params) {
				
			try {
					Map<String,String[]> postParams = new HashMap<String,String[]>();
					postParams.put("username",new String[]{currentUsername});
					postParams.put("regId",new String[]{regId});
					String response = new PostRequest(ServerUtils.SERVER_URL+"/register.php",postParams).request();
					JSONObject reader = new JSONObject(response);
	        		if(reader.getBoolean("success")){
	        			Common.saveSelfUid(currentUsername);
	        			Common.saveExistedRegIdIsRegistered(true);
	        			
	        			return SUCCESS;
	        		}
	        		else{
	        			if(reader.getBoolean("duplicated_username")){
	        				duplicatedName.add(currentUsername);
	        				return DUPLICATED_USERNAME;
	        			}
	        			else 
	        				return UNKNOW_ERROR;
	        		}
	        			
				} catch (Exception e) {
					return UNKNOW_ERROR;
				}
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				switch(result){
				case SUCCESS:
					successfullyRegister();
					break;
				case DUPLICATED_USERNAME:
					//errorTextView.setText(R.string.error_duplicated_username);
					//errorTextView.setVisibility(View.VISIBLE);
					errorTextView.setText(null);
					errorTextView.setVisibility(View.GONE);
					usernameEditText.setError(duplicate_error_msg);
					resetState();
					break;
				case UNKNOW_ERROR:
				default:
					errorTextView.setText(R.string.error_register);
					errorTextView.setVisibility(View.VISIBLE);
					resetState();
					enableStartButton();
					break;
				}
			}
			
		}.execute(new Void[]{});
	}



	@Override
	public void onError() {
		errorTextView.setText(R.string.error_register);
		errorTextView.setVisibility(View.VISIBLE);
		resetState();		
		enableStartButton();
	}

}
