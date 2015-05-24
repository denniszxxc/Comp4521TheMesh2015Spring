package hk.ust.comp4521.instant_messaging.server_utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

public class PostRequest implements HttpRequest{
	
	private static final String TAG = "PostRequest";
	
	String link;
	private byte[] contentBytes;
	private URL url;
	

	
	public PostRequest(String link, Map<String, String[]> params){
		this.link = link;   
        try {
            url = new URL(link);
            
            StringBuilder bodyBuilder = new StringBuilder();
            for (Entry<String, String[]> param: params.entrySet()) { 
            	if(param.getValue().length > 1){
    	            for(String value: param.getValue()){
    	            	bodyBuilder.append(param.getKey()).append("[]=").append(value).append('&');
    	            } 
            	}
            	else if(param.getValue().length == 1)
            		bodyBuilder.append(param.getKey()).append("=").append(param.getValue()[0]).append('&');
            }
            
            bodyBuilder.deleteCharAt(bodyBuilder.length()-1);
            String body = bodyBuilder.toString();
            Log.i("Url: "+link,"Post Params: "+body);
            
            contentBytes = body.getBytes();          
        }
        catch (MalformedURLException e) {
           throw new IllegalArgumentException("Invalid URL: " + link);
        }
	}
	
	@Override
	public String request() throws Exception{
		HttpURLConnection connection = null;
		StringBuffer responseBuilder = new StringBuffer();
        String response = null;
        
        try{            	
        	connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setFixedLengthStreamingMode(contentBytes.length);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            OutputStream out = connection.getOutputStream();
            out.write(contentBytes);
            out.close();
            int status = connection.getResponseCode();
            if (status != 200)
              throw new IOException("Post is failed! Response code: " + status);   
            else {
            	BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        		String line;
        		while ((line = in.readLine()) != null) {
        			responseBuilder.append(line);
        		}
        		response = responseBuilder.toString();
        		Log.i(TAG,"Response: "+response);
        		in.close();
            }
        } 
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + link);
        }
        finally {
        	if (connection != null)
                connection.disconnect();
        }
        return response;		
	}
}
