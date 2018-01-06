package com.example.marcus.grouponadministrator.Two;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.marcus.grouponadministrator.R;
import com.example.marcus.grouponadministrator.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScheduledSearchReceiver extends BroadcastReceiver
{
    private static final boolean DEBUG = true;
    private static final String TAG = "ScheduledSearchReceiver";

    //start at 1, because 0 is reserved for Z_YelpResultsNotification
    private static int startingUniqueRequestCode = GlobalVariables.SCHEDULED_SEARCH_STARTING_NOTIFICATION_ID;

    //all notification created here have to be canceled through here
    //otherwise the arrayList existing RequestCodes would not be able to relinquish the request codes
    public static final String DISMISS = "DISMISS_RECEIVER";

    //stores all the existing notification unique request codes
    private static List<Integer> existingRequestCodes = new ArrayList<>();

    public static class DismissReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(DEBUG) Log.e(TAG, "Dismiss received");

            int requestCode = intent.getIntExtra("requestCode", -1);

            if(requestCode != -1)
            {
                if(DEBUG) Log.e(TAG, "request code to remove: " + requestCode);

                String out =  "";
                for(int i : existingRequestCodes)
                    out += i + " ";
                Log.e(TAG, "before remove: " + out);

                //relinquish request code
                existingRequestCodes.removeAll(Arrays.asList(requestCode));

                out =  "";
                for(int i : existingRequestCodes)
                        out += i + " ";
                Log.e(TAG, "after remove: " + out);

                //Notification Issued by sending them to the NotificationManager system service
                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(requestCode);
            }
            else
            {
                Log.e(TAG, "requestCode is -1, should be 1 to infinity");
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(DEBUG) Log.e(TAG, "onReceive");

        //get tags from intent
        ArrayList<String> tags = intent.getStringArrayListExtra("tags");

        //get unique request code
        int requestCode = getUniqueRequestCode();
        existingRequestCodes.add(requestCode);
        createStartNotification(context, tags, requestCode);
    }

    private static boolean requestCodeIsUnique(int requestCode)
    {
        for(int i : existingRequestCodes)
        {
            if(i == requestCode)
                return false;
        }
        return true;
    }
    private static int getUniqueRequestCode()
    {
        //start at one because, 0 is reserved for YelpRequestsNotification
        int i = startingUniqueRequestCode;
        while(!requestCodeIsUnique(i))
        {
            i++;
        }

        if(DEBUG) Log.e(TAG, "request code returned: " + i);

        return i;
    }

    private static void createStartNotification(Context context, ArrayList<String> tags, int uniqueRequestCode)
    {
        if(DEBUG) Log.e(TAG, "createStartNotification");

        //get large icon
        Bitmap largeIcon = Utility.getLargeIcon(context);

        //create content text
        String contentText = "";
        for(String s : tags)
            contentText += s + " ";

        //set start intent, pending intent, action
        //TODO mess with Stack
        Intent iStart = new Intent(context, YelpResultsActivity.class);
        iStart.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        // needs to be added for some reason so receiving activity may get the actual request code and tags
        iStart.setAction(Integer.toString(uniqueRequestCode));

        Log.e(TAG, "request code in intent is: " + uniqueRequestCode);
        iStart.putExtra("requestCode", uniqueRequestCode);
        iStart.putStringArrayListExtra("tags", tags);
        //0 - unique request code needed
        PendingIntent piStart = PendingIntent.getActivity(context, 0, iStart, PendingIntent.FLAG_UPDATE_CURRENT); //PendingIntent.FLAG_ONE_SHOT
        NotificationCompat.Action actionStart = new NotificationCompat.Action.Builder(R.drawable.ic_play_arrow_black_24dp, "START", piStart).build();

        //set dismiss intent, pending intent, action
        Intent iDismiss = new Intent(DISMISS);
        iDismiss.putExtra("requestCode", uniqueRequestCode);
        PendingIntent piDismiss = PendingIntent.getBroadcast(context, 1, iDismiss, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action actionDismiss = new NotificationCompat.Action.Builder(R.drawable.ic_not_interested_black_24dp, "DISMISS", piDismiss).build();

        //create notification, and add start and dismiss action
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ghoomo_icon_small)
                        .setLargeIcon(largeIcon)
                        .setContentTitle("GHOOMO")
                        .setContentText(contentText)
                        .addAction(actionStart)
                        .addAction(actionDismiss)
                        .setAutoCancel(true);

        //create pending intent
        //mBuilder.setContentIntent();

        //get system notification manager
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(uniqueRequestCode, mBuilder.build());
    }
}
