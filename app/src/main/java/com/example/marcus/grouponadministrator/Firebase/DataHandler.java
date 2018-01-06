package com.example.marcus.grouponadministrator.Firebase;

import android.content.Context;

import com.example.marcus.grouponadministrator.Config.FirebaseVariable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Marcus on 8/29/2016.
 */
public class DataHandler {

    public static void handleIt(Context context, String jsonString) {
        //get the data type, to figure out what to do with data
        try {
            //get data type integer
            JSONObject jo = new JSONObject(jsonString);
            jo  = jo.getJSONObject(FirebaseVariable.DATA);
            int data_type = jo.getInt(FirebaseVariable.DATA_TYPE);

            //if data type is a groupon notification
            if(data_type == FirebaseVariable.GROUPON_NOTIFICATION) {
                // parse and send groupon notification
                sendGrouponNotification(context, jo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void sendGrouponNotification(Context context, JSONObject jo) {
        try {
            //get deal id
            String dealID = jo.getString(FirebaseVariable.ID);

            //create new groupon notification class and pass the local variables into constructor
            GrouponNotification grouponNotification = new GrouponNotification(context, dealID);

            //initiate send notification to appear on device
            grouponNotification.sendNotification();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
