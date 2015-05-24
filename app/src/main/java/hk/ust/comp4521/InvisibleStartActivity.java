package hk.ust.comp4521;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class InvisibleStartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = null;
		if(Common.getSelfUid().equals(""))
			intent = new Intent(this,RegistrationActivity.class);
		else
			intent = new Intent(this,MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}
	

}
