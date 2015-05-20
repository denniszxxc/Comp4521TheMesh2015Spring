package com.comp4521.bookscan;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class GetBookInfo {
	private String TAG = "GetBookInfo";
	private Context mContext;
	private String getBookDataUrl = "https://www.googleapis.com/books/v1/volumes?q=";
	private String key = "&key=AIzaSyBwMm64PPxzXIx3GmkOVA9CV-5GHkf2UOI";
	private ArrayList<JSONObject> bookData = new ArrayList<JSONObject>();
	private ArrayList<ToGetBookDataTask> getBookDataTask = new ArrayList<ToGetBookDataTask>(); 
	private AlertDialog mDialog;
	private ArrayList<String> bookResult = new ArrayList<String>();
	
    public GetBookInfo(Context context) {
    	mContext = context;
    	mDialog = new AlertDialog.Builder(mContext).create();
    	long totalInput = ScannerFragment.os.getTotalInputValue();
    	for(long i=0; i<totalInput; i++) {
    		getBookDataTask.add(null);
    		bookData.add(null);
    	}
	}
    
    public void setBookResult(ArrayList<String> bookResult) {
    	this.bookResult = bookResult;
    }
    
    public ArrayList<String> getBookResult() {
    	if(bookResult != null)
    		return bookResult;
    	else
    		return new ArrayList<String>();
    }
    
	public boolean runGetBookDataTask(String iSBN) {
	    ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    if (networkInfo != null && networkInfo.isConnected()) {
	    	Log.i(TAG, "Connection: try to get bookData");
	    	long totalInput = ScannerFragment.os.getTotalInputValue();
	    	getBookDataTask.add(new ToGetBookDataTask());
	    	getBookDataTask.get(Long.valueOf(totalInput).intValue()).execute(iSBN);
	    	bookData.add(null);
	    	ScannerFragment.os.setTotalInputValue(++totalInput);
	    	return true;
	    } else {
	    	Log.e(TAG, "Connection: No network connection available.");
			if (mDialog.isShowing())
				return false;
	    	mDialog.setTitle("Connection Problem");
	    	mDialog.setMessage("No network connection available.");
	    	mDialog.show();
	    	return false;
	    }
	}
	
	public boolean getDialogState() {
		return mDialog.isShowing();
	}
	
    public void stopGetBookDataTaskOperation(){
    	mDialog.dismiss();
        if(getBookDataTask.size() != 0){
        	for(int i=0; i<getBookDataTask.size(); i++)
        		if(getBookDataTask.get(i).getStatus().equals(AsyncTask.Status.RUNNING)){
        			Log.i(TAG, "AsyncTask: getBookDataTask cancel!");
        			getBookDataTask.get(Long.valueOf(i).intValue()).cancel(true);
        		}
        }
    }
	
    private class ToGetBookDataTask extends AsyncTask<String, Void, JSONObject> {
        private static final String DEBUG_TAG = "GetBookInfoTask";
        String iSBN = "";
        
		@Override
        protected JSONObject doInBackground(String... iSBNs)  {
            try {
            	Log.i(DEBUG_TAG, "getting BookData");
            	iSBN = iSBNs[0];
				return getBookData(getBookDataUrl+iSBNs[0]+key);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return new JSONObject();
			}
        }
		
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(JSONObject result) {
        	long totalInput = ScannerFragment.os.getTotalInputValue();
        	bookData.set(Long.valueOf(totalInput-1).intValue(), result);
        	if(bookData.get(Long.valueOf(totalInput-1).intValue()) != null) {
            	JSONObject jObj = bookData.get(Long.valueOf(totalInput-1).intValue());
            	JSONArray jArr = null;
            	JSONObject jObjInItemArr = null;
            	JSONObject jObjInVolumeInfo = null;
            	try {
					jArr = (JSONArray) jObj.get("items");
	            	jObjInItemArr = jArr.getJSONObject(0);
	            	jObjInVolumeInfo = jObjInItemArr.getJSONObject("volumeInfo");
	            	String title = jObjInVolumeInfo.getString("title");
	            	JSONArray authorsArr = jObjInVolumeInfo.getJSONArray("authors");
	            	String authors = "";
	            	for(int i=0; i<authorsArr.length(); i++) {
	            		authors += authorsArr.get(i).toString();
	            		if(i != authorsArr.length()-1)
	            			authors += ", ";
	            	}
	            	String imageLink = "noLink";
	            	try {
	            		imageLink = jObjInVolumeInfo.getJSONObject("imageLinks").getString("thumbnail");
	            	} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

	    	    	long success = ScannerFragment.os.getFoundValue();
	            	ScannerFragment.os.setFoundValue(++success);

	            	// assume must found these three things
	            	bookResult.add(title +"@"+
	            			authors +"@"+
	            			imageLink +"@"+ iSBN);
	            	
	            	Log.i(DEBUG_TAG, "Title: " + title + "\nAuthors: " + authors + "\nImageLink: " + imageLink + "\nISBN: " + iSBN);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
        
        // get an encoded JSON data
		private JSONObject getBookData(String myurl) throws IOException {
			JSONObject json = null;
			try {
				String result = doGet(myurl);
				if(result != "")
					json = new JSONObject(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;
		}
		
		// send get request to get Stream
        private String doGet(String myurl) throws IOException {
            InputStream is = null;
            try {
                is = sendHTTPSRequest(myurl);

                // Convert the InputStream into a string
                if(is != null) {
                	String contentAsString = convertStreamToString(is);
                	return contentAsString;
                }
                return "";
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                } 
            }
        }
        
        // Function1 to make Stream To String
        private String convertStreamToString(InputStream is) {
	        ByteArrayOutputStream oas = new ByteArrayOutputStream();
	        copyStream(is, oas);
	        String t = oas.toString();
	        try {
	            oas.close();
	            oas = null;
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        return t;
	    }
        
        // Function2 to make Stream To String
	    private void copyStream(InputStream is, OutputStream os) {
	        final int buffer_size = 1024;
	        try {
	            byte[] bytes=new byte[buffer_size];
	            for(;;) {
	            	int count = is.read(bytes, 0, buffer_size);
	            	if(count == -1)
	            		break;
	            	os.write(bytes, 0, count);
	            }
	        }
	        catch(Exception ex) {}
	    }
    }
    
    private InputStream sendHTTPSRequest(final String myurl) {
		URL url;
		InputStream in = null;
		try {
			url = new URL(myurl);
			HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
			urlConnection.setReadTimeout(10000 /* milliseconds */);
			urlConnection.setConnectTimeout(15000 /* milliseconds */);
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();
            int response = urlConnection.getResponseCode();
            Log.d(TAG, "HTTPS: The response is: " + response);
            if(response != 400)
            	in = urlConnection.getInputStream();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return in;
	}
}