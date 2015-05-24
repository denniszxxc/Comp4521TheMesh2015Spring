package com.comp4521.bookscan.MainLayout;

/**
 * Created by dennisli on 24/5/15.
 */

import com.helper.datatoserver.DataToServerFunction;
import org.json.JSONException;
import org.json.JSONObject;

import hk.ust.comp4521.Common;

public class WriteToServer {
    public JSONObject removeOneBook(String userID, String bookID) {
        JSONObject toSend = new JSONObject();
        try {
            toSend.put("handle_method", "DeleteBook"); // this is an important data to indicate what function will be called in server
            toSend.put("user_id", Common.getSelfUid()); // TODO: need to be changed, to get this infomation from shared Preference
            toSend.put("book_id", bookID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        DataToServerFunction dataToServer = new DataToServerFunction(); // send json data to server though this class
        return dataToServer.sendDataToServer(toSend, true, true);
    }

    public JSONObject borrowOneBook(String userID, String bookID, String lendeID) {
        JSONObject toSend = new JSONObject();
        try {
            toSend.put("handle_method", "confirmBookBorrow"); // this is an important data to indicate what function will be called in server
            toSend.put("borrower_user_id", Common.getSelfUid()); // TODO: need to be changed, to get this infomation from shared Preference
            toSend.put("lender_user_id", lendeID);
            toSend.put("book_id", bookID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        DataToServerFunction dataToServer = new DataToServerFunction(); // send json data to server though this class
        return dataToServer.sendDataToServer(toSend, true, true);
    }

    public JSONObject borrowOneBookCall2(String userID, String bookID, String lendeID) {
        JSONObject toSend = new JSONObject();
        try {
            toSend.put("handle_method", "ChangeBookStatus"); // this is an important data to indicate what function will be called in server
            toSend.put("lender_user_id", lendeID); // TODO: need to be changed, to get this infomation from shared Preference
            toSend.put("target_status", 0);
            toSend.put("book_id", bookID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        DataToServerFunction dataToServer = new DataToServerFunction(); // send json data to server though this class
        return dataToServer.sendDataToServer(toSend, true, true);
    }

    public JSONObject deleteConfirmBookBorrow(String userID, String bookID) {
        JSONObject toSend = new JSONObject();
        try {
            toSend.put("handle_method", "deleteConfirmBookBorrow"); // this is an important data to indicate what function will be called in server
            toSend.put("lender_user_id", Common.getSelfUid()); // TODO: need to be changed, to get this infomation from shared Preference
            toSend.put("book_id", bookID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        DataToServerFunction dataToServer = new DataToServerFunction(); // send json data to server though this class
        return dataToServer.sendDataToServer(toSend, true, true);
    }


    public Boolean checkBookBorrow(String userID, String bookID ) {
        JSONObject toSend = new JSONObject();
        try {
            toSend.put("handle_method", "CheckConfirmBookBorrow"); // this is an important data to indicate what function will be called in server
            toSend.put("lender_user_id", Common.getSelfUid()); // TODO: need to be changed, to get this infomation from shared Preference
            toSend.put("book_id", bookID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        DataToServerFunction dataToServer = new DataToServerFunction(); // send json data to server though this class
        String result = new String();
        try {
            result =  dataToServer.sendDataToServer(toSend, true, true).getString("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result.equals("true");
    }


}
