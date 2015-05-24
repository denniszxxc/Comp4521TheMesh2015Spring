package hk.ust.comp4521;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class PlayServiceCantSolvedAlert extends DialogFragment {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Activity mActivity = getActivity();
		return new AlertDialog.Builder(mActivity).setTitle("Error").setMessage(R.string.error_play_service).setNeutralButton(R.string.action_close_app,new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mActivity.finish();			
			}
		}).setCancelable(false).create();
	}

}
