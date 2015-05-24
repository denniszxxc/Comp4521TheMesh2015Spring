package com.comp4521.bookscan.MainLayout;

import com.helper.datatoserver.DataToServerFunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.helper.datatoserver.DataToServerFunction;

import hk.ust.comp4521.Common;

/**
 * Created by dennisli on 24/5/15.
 */
public class ReadFromServer {
    public JSONObject getBookListOfOneUser(String targetUserID) {
        JSONObject toSend = new JSONObject();
        try {
            toSend.put("handle_method", "GetAListOfBookFromTheUser"); // this is an important data to indicate what function will be called in server
            toSend.put("target_user_id", Common.getSelfUid()); // TODO: need to be changed, to get this infomation from shared Preference

        } catch (JSONException e) {
            e.printStackTrace();
        }
        DataToServerFunction dataToServer = new DataToServerFunction(); // send json data to server though this class
        return dataToServer.sendDataToServer(toSend, true, true);
    }

    public JSONObject getBookListAll(String targetUserID) {
        JSONObject toSend = new JSONObject();
        try {
            toSend.put("handle_method", "GetAListOfBooks"); // this is an important data to indicate what function will be called in server
            toSend.put("user_id", Common.getSelfUid()); // TODO: need to be changed, to get this infomation from shared Preference

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DataToServerFunction dataToServer = new DataToServerFunction(); // send json data to server though this class
        return dataToServer.sendDataToServer(toSend, true, true);
    }

    public JSONObject checkBookBorrowed(String targetUserID) {
        JSONObject toSend = new JSONObject();
        try {
            toSend.put("handle_method", "GetAListOfBooks"); // this is an important data to indicate what function will be called in server
            toSend.put("user_id", Common.getSelfUid()); // TODO: need to be changed, to get this infomation from shared Preference

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DataToServerFunction dataToServer = new DataToServerFunction(); // send json data to server though this class
        return dataToServer.sendDataToServer(toSend, true, true);
    }
}