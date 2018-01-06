package com.example.marcus.grouponadministrator.Two;

import android.app.Activity;
import android.app.Notification;
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

/**
 * Created by Marcus Chiu on 4/26/2016.
 */
public class YelpResultsNotification// implements LoaderManager.LoaderCallbacks<Bitmap>
{
    //bottom 2 are for debugging purposes
    private static final String TAG = "YelpResultsNotification";
    private static final boolean DEBUG = false;

    private static final String ACTION_NOTIFICATION = "GHOOMO_NOTIFICATION_ACTION_2";
    private static final String ACTION = "action";

    private enum NotificationAction {LIST, EXIT, CLICKED}
    private static final int NotificationActionSize = 5;

    private static final int IMAGE_LOADER_ID = 0;

    private String CONTENT_TITLE;
    private String CONTENT_TEXT;

    private String IMAGE_CONTEXT_TITLE;
    private String IMAGE_CONTEXT_TEXT;
    private String IMAGE_STRING_URL;

    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private final int notificationID = GlobalVariables.YELP_NOTIFICATION_ID;

    private static OnYelpResultsNotification mListener;
    private Activity activity;

    public static class NotificationReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int i = intent.getIntExtra(ACTION, -1);

            if(i < 0 || i > NotificationActionSize-1) return;

            NotificationAction action = NotificationAction.values()[i];

            switch(action)
            {
                case CLICKED:
                    if(mListener != null)
                        mListener.OnNotificationClicked();
                    break;

                case LIST:
                    if(mListener != null)
                        mListener.OnNotificationList();
                    break;

                case EXIT:
                    if(mListener != null)
                        mListener.OnNotificationExit();
                    //mListener = null;
                    //destroyNotification();
                    break;
            }
        }
    }

    public YelpResultsNotification(Activity activity)
    {
        CONTENT_TITLE = "GHOOMO";
        CONTENT_TEXT = "robust notification :)";

        IMAGE_CONTEXT_TITLE = "";
        IMAGE_CONTEXT_TEXT = "";
        IMAGE_STRING_URL = "";

        this.activity = activity;

        //set mListener Interface
        if (activity instanceof OnYelpResultsNotification)
            mListener = (OnYelpResultsNotification) activity;
        else
            throw new RuntimeException(activity.toString() + " must implement OnYelpResultsNotification");
    }

    public void createNotification()
    {
        if(DEBUG) Log.i(TAG, "creating notification");

        Bitmap largeIcon = Utility.getLargeIcon(activity);

        //Notification Issued by sending them to the NotificationManager system service
        mNotificationManager = (NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent piList = createPendingIntent(activity, NotificationAction.LIST);
        NotificationCompat.Action actionList = new NotificationCompat.Action.Builder(R.drawable.ic_view_list_black_24dp, "SHOW LIST", piList).build();

        PendingIntent piExit = createPendingIntent(activity, NotificationAction.EXIT);
        NotificationCompat.Action actionExit = new NotificationCompat.Action.Builder(R.drawable.ic_clear_black_24dp, "EXIT", piExit).build();

        //Instantiate notification builder
        mBuilder = new NotificationCompat.Builder(activity)
                .setSmallIcon(R.drawable.ghoomo_icon_small)
                .setLargeIcon(largeIcon)
                .setContentText(CONTENT_TEXT)
                .setContentTitle(CONTENT_TITLE)
                .setPriority(android.support.v7.app.NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .addAction(actionList)
                .addAction(actionExit)
                .setOngoing(true);
        //.setAutoCancel(true);

        //create a fixed duration progress bar
        //seems like the progress bar is always displayed right below the notification title
        //createFixedDurationProgressBar(1);

        //create a continuing activity indicator
        //createContinuingActivityProgressBar(5);

        //Define action
        //Intent resultIntent = new Intent(context, Main2Activity.class);
        //resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent piClicked = createPendingIntent(activity, NotificationAction.CLICKED);
        mBuilder.setContentIntent(piClicked);

        //set pending intent whenever notification is canceled
        //mBuilder.setDeleteIntent(piExit);

        //Build notification and pass it to the NotificationManager
        mNotificationManager.notify(notificationID, mBuilder.build());
    }

    private static PendingIntent createPendingIntent(Context context, NotificationAction a)
    {
        Intent i = new Intent(ACTION_NOTIFICATION);
        i.putExtra(ACTION, a.ordinal());
        return PendingIntent.getBroadcast(
                context,
                a.ordinal(),//unique request code needed
                i,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void updateImage(String imageURL, String contentTitle, String contentText)
    {
        if(DEBUG) Log.i(TAG, "updatingImage URL");

        //TODO
        Log.e("YelpNotifications", "work needs to be done here");
        if(mBuilder == null) createNotification();

        /*if(bitmap != null)
        {
            //Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle().bigPicture(bitmap);
            bigPictureStyle.setBigContentTitle(contentTitle);
            bigPictureStyle.setSummaryText(contentText);
            mBuilder.setStyle(bigPictureStyle);

            //update notification and pass it to the NotificationManager
            mNotificationManager.notify(notificationID, mBuilder.build());
        /*}
        else
        {
            if(DEBUG) Log.e(TAG, "updateImage: drawable is null");
        }*/
        /*
        IMAGE_CONTEXT_TITLE = contentTitle;
        IMAGE_CONTEXT_TEXT = contentText;
        IMAGE_STRING_URL = imageURL;

        activity.getLoaderManager().initLoader(IMAGE_LOADER_ID, null, this);*/

    }

    //ImageLoader Interface
    /*@Override
    public Loader<Bitmap> onCreateLoader(int id, Bundle args)
    {
        return new ImageLoader(activity, IMAGE_STRING_URL);
    }
    @Override
    public void onLoadFinished(Loader<Bitmap> loader, Bitmap data)
    {
        if(DEBUG) Log.i(TAG, "onLoadFinished");
        updateImage(data, IMAGE_CONTEXT_TITLE, IMAGE_CONTEXT_TEXT);
    }
    @Override
    public void onLoaderReset(Loader<Bitmap> loader)
    {

    }*/

    public void updateImage(Bitmap bitmap, String contentTitle, String contentText)
    {
        if(mBuilder == null) createNotification();

        if(bitmap != null)
        {
            //Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle().bigPicture(bitmap);
            bigPictureStyle.setBigContentTitle(contentTitle);
            bigPictureStyle.setSummaryText(contentText);
            mBuilder.setStyle(bigPictureStyle);

            //update notification and pass it to the NotificationManager
            mNotificationManager.notify(notificationID, mBuilder.build());
        }
        else
        {
            if(DEBUG) Log.e(TAG, "updateImage: drawable is null");
        }
    }

    public void destroyNotification()
    {
        if(DEBUG) Log.i(TAG, "OnDestroy");

        mNotificationManager.cancel(notificationID);
    }

    public interface OnYelpResultsNotification
    {
        void OnNotificationExit();
        void OnNotificationClicked();
        void OnNotificationList();
    }

    /*private void createFixedDurationProgressBar(final int seconds)
    {
        // Start a lengthy operation in a background thread
        new Thread(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        int incr;
                        // Do the "lengthy" operation 20 times
                        for (incr = 0; incr <= 100; incr+=5)
                        {
                            // Sets the progress indicator to a max value, the
                            // current completion percentage, and "determinate"
                            // state
                            mBuilder.setProgress(100, incr, false);
                            // Displays the progress bar for the first time.
                            mNotificationManager.notify(notificationID, mBuilder.build());
                            // Sleeps the thread, simulating an operation
                            // that takes time
                            try
                            {
                                // Sleep for 1 seconds
                                Thread.sleep(seconds *1000);
                            }
                            catch (InterruptedException e)
                            {
                                Log.d(TAG, "sleep failure");
                            }
                        }
                        // When the loop is finished, updates the notification
                        mBuilder.setContentText("Download complete")
                                // Removes the progress bar
                                .setProgress(0,0,false);
                        mNotificationManager.notify(notificationID, mBuilder.build());
                    }
                }
                // Starts the thread by calling the run() method in its Runnable
        ).start();
    }

    private void createContinuingActivityProgressBar(final int seconds)
    {
        // Start a lengthy operation in a background thread
        new Thread(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        int incr;
                        // Do the "lengthy" operation 20 times
                        for (incr = 0; incr <= 100; incr+=5)
                        {
                            // Sets an activity indicator for an operation of indeterminate length
                            mBuilder.setProgress(0, 0, true);
                            // Issues the notification
                            mNotificationManager.notify(notificationID, mBuilder.build());
                            // Sleeps the thread, simulating an operation
                            // that takes time
                            try
                            {
                                // Sleep for 1 seconds
                                Thread.sleep(seconds *1000);
                            }
                            catch (InterruptedException e)
                            {
                                Log.d(TAG, "sleep failure");
                            }
                        }
                        // When the loop is finished, updates the notification
                        mBuilder.setContentText("Download complete")
                                // Removes the progress bar
                                .setProgress(0,0,false);
                        mNotificationManager.notify(notificationID, mBuilder.build());
                    }
                }
                // Starts the thread by calling the run() method in its Runnable
        ).start();
    }*/
}
