package com.comp4521.bookscan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.helper.datatoserver.DataToServerFunction;

public class BookInfoToServer {
	public JSONObject callInBookListConfirmFragment(JSONArray bookIds, JSONArray bookResultJSON, String time, String type) {
		JSONObject toSend = new JSONObject();
		try {
			toSend.put("handle_method", "BookListConfirm"); // this is an important data to indicate what function will be called in server
			toSend.put("user_id", "C0mPC0mPC0mPC0mP"); // need to be changed, to get this infomation from shared Preference
			toSend.put("book_data", bookResultJSON);
			toSend.put("added_time", time);
			toSend.put("offer_type", type);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DataToServerFunction dataToServer = new DataToServerFunction(); // send json data to server though this class
		return dataToServer.sendDataToServer(toSend, true, true);
	}
}