package com.example.marcus.grouponadministrator.Firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.marcus.grouponadministrator.Config.DatabaseVariable;
import com.example.marcus.grouponadministrator.Database.Deal;
import com.example.marcus.grouponadministrator.Database.PostProcessor;
import com.example.marcus.grouponadministrator.One.MainFeedActivity;
import com.example.marcus.grouponadministrator.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

/**
 * Created by Marcus on 8/29/2016.
 */
public class GrouponNotification {
    private static final int NOTIFICATION_ID = 0;

    private Context context;
    private String dealID;

    public GrouponNotification(Context context, String dealID) {
        this.context = context;
        this.dealID = dealID;
    }

    public void sendNotification() {
        try {
            //get deal from database by ID
            HashMap<String, String> params = new HashMap<>();
            params.put(DatabaseVariable.ID, dealID);
            String jsonString = PostProcessor.load(new URL(DatabaseVariable.GET_DEAL_BY_ID), params);

            //parse json string into deal object
            JSONObject jo = new JSONObject(jsonString);
            JSONArray ja =  jo.getJSONArray(DatabaseVariable.DEAL);
            Deal deal = new Deal(ja.getJSONObject(0));

            //load image from url synchronously
            Bitmap bm = Picasso.with(context).load(deal.getImageURL()).get();

            //if valid bitmap display image notification, otherwise show plain notification
            if(bm != null) {
                showImageNotification(bm, deal.getBoldText(), deal.getFinePrintText());
            } else {
                showNotification(deal.getBoldText(), deal.getFinePrintText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //display notification with image
    public void showImageNotification(Bitmap bm, String title, String text) {
        Intent intent = new Intent(context, MainFeedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ghoomo_icon_small);
        //Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.sushi);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ghoomo_icon_small)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bm));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID /* ID of notification */, notificationBuilder.build());
    }

    //display plain notification without image
    public void showNotification(String title, String text) {
        Intent intent = new Intent(context, MainFeedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ghoomo_icon_small);
        //Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.sushi);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ghoomo_icon_small)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID /* ID of notification */, notificationBuilder.build());
    }
}
