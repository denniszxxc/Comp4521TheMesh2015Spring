package com.comp4521.bookscan;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.bookscan.R;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class BookListConfirmFragment extends ListFragment {
	public final static String TAG = "BookListConfirmFragment";
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private Context activityContext;
    private View rootView;
	private ArrayList<String> bookResult = new ArrayList<String>();
	private Dialog backButtonDialog = null;
	private Button btnfirm;
	private JSONArray bookResultJSON = new JSONArray();
	private JSONArray bookIds = new JSONArray();
	private boolean isUploading = false;
	private boolean bookResultFromArgs = false;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	Log.i(TAG, "BookListConfirmFragment onCreateView()!");
        rootView = inflater.inflate(R.layout.book_confirm, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        return rootView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        dataFromOtherFragment();
        if(!bookResultFromArgs)
	        if(savedInstanceState != null) {
	            ArrayList<String> LongTest = savedInstanceState.getStringArrayList("bookInfo_bookResult");
	            Log.d(TAG, "BookListConfirmFragment get savedInstanceState LongTest="+LongTest.toString());        
	        }
    	Log.i(TAG, "BookListConfirmFragment onActivityCreated()!");
        super.onActivityCreated(savedInstanceState);
        activityContext = getActivity();
        addScannedBooks();
        setAllListener();
        adapter = new ArrayAdapter<String>(activityContext, R.layout.scan_book_list, list);
        setListAdapter(adapter);
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	// Save away the original text, so we still have it if the activity
    	// needs to be killed while paused.
    	savedInstanceState.clear();
    	savedInstanceState.putStringArrayList("bookInfo_bookResult", bookResult);
    	super.onSaveInstanceState(savedInstanceState);
    	Log.d(TAG, "BookListConfirmFragment onSaveInstanceState()!");
    }
    
	@Override
	public void onDestroyView() {
		Log.i(TAG, "BookListConfirmFragment onDestroyView()!");
		super.onDestroyView();
	}
	
    private void dataFromOtherFragment() {
        Bundle args = getArguments();
        if (args != null) {
        	bookResult = args.getStringArrayList("bookInfo_bookResult");
        	bookResultFromArgs = true;
        	Log.i(TAG, "BookListConfirmFragment get dataFromOtherFragment()!");
        }
    }
    
    private void addScannedBooks() {
    	if(bookResult.size() != 0) {
    		TextView emptyTxt = (TextView) rootView.findViewById(R.id.empty);
    		emptyTxt.setVisibility(View.GONE);  
    	}

		for(int i=0; i<bookResult.size(); i++) {
			String[] items = bookResult.get(i).split("@");
			list.add(items[0] + " / \n" + items[1]);
//			items[2] = items[2].replace('&', '^'); // change some data's sign here
//			bookResultJSON.put(items[0] +"@"+ items[1] +"@"+ items[2] +"@"+ items[3]);
		}
	}
    
    private void setAllListener() {
        btnfirm = (Button) rootView.findViewById(R.id.btnfirm);
        Button btnDel = (Button) rootView.findViewById(R.id.btnDel);
        OnClickListener listenerFirm = new OnClickListener() {
            @Override
            public void onClick(View v) {
            	v.setEnabled(false); 
            	showDialog();
            }
        };
        OnClickListener listenerDel = new OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT) 
            @Override
            public void onClick(View v) {
                /** Getting the checked items from the listview */
                SparseBooleanArray checkedItemPositions = getListView().getCheckedItemPositions();
                int itemCount = getListView().getCount();
                Log.i(TAG, Integer.toString(itemCount));
                
                for(int i=itemCount-1; i >= 0; i--){
                    if(checkedItemPositions.get(i)){
                    	Log.i(TAG, "Selected item: " + Integer.toString(i));
                        adapter.remove(list.get(i));
						bookResult.remove(i);
						// Log.i("TEST JSON remove",String.valueOf(i) + "  \n result:" + bookResultJSON.toString()  + "\n Bookresult:" + bookResult.toString());
						// bookResultJSON.remove(i);
					}
                }
                checkedItemPositions.clear();
                adapter.notifyDataSetChanged();
            }
        };
        btnfirm.setOnClickListener(listenerFirm);
        btnDel.setOnClickListener(listenerDel);
    }
    
    private void showDialog() {
        // Use the Builder class for convenient dialog construction
    	AlertDialog.Builder mBuilder = new AlertDialog.Builder(activityContext);
        mBuilder.setMessage("Are you sure to save?");
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        				isUploading = true;
        				backButtonListener();
        				new uploadDataTask().execute();
                   }
               });
        mBuilder.setNegativeButton("Cancel", null);
        // Create the AlertDialog object
        Dialog dialog = mBuilder.create();
		if (dialog.isShowing())
			return;
		else {
			dialog.setOnDismissListener(new OnDismissListener() {
		        @Override
		        public void onDismiss(final DialogInterface arg0) {
		        	if(!isUploading)
		        		btnfirm.setEnabled(true);
		        }
		    });
			dialog.show();
		}
    }
    
	private void backButtonListener() {
    	// need to do some to check if the user click the back button on the device
    	rootView.setFocusableInTouchMode(true);
    	rootView.requestFocus();
    	rootView.setOnKeyListener(new OnKeyListener() {
    		@Override
    		public boolean onKey(View v, int keyCode, KeyEvent event) {
	    		if (event.getAction() == KeyEvent.ACTION_DOWN) {
		    		if (keyCode == KeyEvent.KEYCODE_BACK) {
		    			showBackButtonDialog();
			    	}
			    }
		    	return false;
	    	}
    	});
	}
	
    private void showBackButtonDialog() {
        // Use the Builder class for convenient dialog construction
    	AlertDialog.Builder mBuilder = new AlertDialog.Builder(activityContext);
        mBuilder.setMessage("Data is uploading... Are you sure to leave? If Yes, all scanned book data may be gone!");
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	Intent intent = new Intent(getActivity(), com.comp4521.bookscan.MainLayout.MainActivity.class); // the class may be different
            	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("toFragment", "My Library");
            	startActivity(intent);
            }
        });
        mBuilder.setNegativeButton("Cancel", null);
        // Create the AlertDialog object
        backButtonDialog = mBuilder.create();
		if (!backButtonDialog.isShowing())
			backButtonDialog.show();
    }
    
    private class uploadDataTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			storeData();
			return null;
		}
    }
    
    private void storeData() {
    	if(bookResult.size() != 0) {
	    	Intent intentForGetType = getActivity().getIntent();
	    	String type = intentForGetType.getStringExtra("type");
	    	Log.i(TAG, "Book Type: " + type);
	    	String time = new Date().toString();
	    	JSONObject receivedJson = dataToServer(time, type); //send data to server
	    	if(receivedJson != null) {
		    	try {
					bookIds = receivedJson.getJSONArray("book_ids");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		dataToSQLite(time, type); //put data into SQLite
	    	}
	    }
    	
    	if(backButtonDialog != null)
    		if (backButtonDialog.isShowing())
    			backButtonDialog.dismiss();
    	
    	//back to last activity
    	Intent intent = new Intent(getActivity(), com.comp4521.bookscan.MainLayout.MainActivity.class); // the class may be different
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("toFragment", "My Library");

		startActivity(intent);
    }
    
    private void dataToSQLite(String time, String type) { // not yet finish
    	BookInfoToSQLite dataToSQLite = new BookInfoToSQLite(activityContext);
    	for(int i=0; i<bookResult.size(); i++) {
    		String[] items = bookResult.get(i).split("@");
    		long bookId = dataToSQLite.searchBookByISBN(items[3]);
    		Log.i(TAG, "bookId: " + Long.toString(bookId));
        	if(bookId == -1) {
        		try {
					bookId = bookIds.getLong(i);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		dataToSQLite.insertDataToBookInfo(bookId, items[0], items[1], items[2], items[3]);
        		if(isExternalStorageWritable() && !items[2].equals("noLink")) {
        			downloadImageFromUrl(items[0] + ".jpg", items[2]);
        			Log.i(TAG, "download image done!");
        		}
        		dataToSQLite.insertDataToOwnerBookInfo(bookId, time, type); // put type as parameter, it means add or borrow
        	} else {
        		int[] numOfBookAndAvailable = dataToSQLite.searchOwnerBookDataByIdAndType(bookId, type);
        		if(numOfBookAndAvailable != null) {
        			Log.i(TAG, "numOfBookAndAvailable: " + Integer.toString(numOfBookAndAvailable[0]) + ", " + Integer.toString(numOfBookAndAvailable[1]));
        			dataToSQLite.updateDataInOwnerBookInfoById(bookId, numOfBookAndAvailable, time);
        		} else
        			dataToSQLite.insertDataToOwnerBookInfo(bookId, time, type);
        	}
    	}
    	dataToSQLite.close();
    	Log.i(TAG, "upload in SQLite done!");
    }
    
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    
    private void downloadImageFromUrl(String fileName, String uRLStr) { //this is the downloader method
		try {
			File extDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); //put the downloaded file here
			Log.i(TAG, extDir.toString());
			URL url = new URL(uRLStr); //you can write here any link
			//File file = new File(mContext.getFilesDir(), fileName); // this put it in data/data/com.example.bookscan/... , it cannot open in real device, except the device is rooted
			File file = new File(extDir, fileName);
			URLConnection ucon = url.openConnection();
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.close();
		} catch (IOException e) {
			Log.d(TAG, "Error: " + e);
		}
	}
    
    private JSONObject dataToServer(String time, String type) {
		for(int i=0; i<bookResult.size(); i++) {
			String[] items = bookResult.get(i).split("@");
//			list.add(items[0] + " / \n" + items[1]);
			items[2] = items[2].replace('&', '^'); // change some data's sign here
			bookResultJSON.put(items[0] +"@"+ items[1] +"@"+ items[2] +"@"+ items[3]);
		}

		BookInfoToServer bookInfoToServer = new BookInfoToServer();
    	JSONObject receivedJson = bookInfoToServer.callInBookListConfirmFragment(bookIds, bookResultJSON, time, type);
    	Log.i(TAG, "upload in server done!");
    	return receivedJson;
    }
}