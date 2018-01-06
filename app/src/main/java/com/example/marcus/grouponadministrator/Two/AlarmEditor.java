package com.example.marcus.grouponadministrator.Two;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Adrian on 4/14/2016.
 */
public class AlarmEditor
{
    private static final boolean DEBUG = true;
    private static final String TAG = "AlarmEditor";

    //The BroadcastReceiver we use will listen for this action
    final static String ACTION_TO_SCHEDULED_SEARCH_RECEIVER = "this_is_for_scheduled_search_receiver";

    /**We need a context to set the alarm (pass the activity instance)
    * Include the tags so we can include append them to the search terms
    * requestCode must be unique and stored in a file
    * Give dayOfWeek in the range 1 to 7 (Sunday to Saturday)
    * Give hour in military time in the range 0 to 23 (12 AM/midnight to 11 PM/before midnight)
    * Give minute in the range 0 to 59 (0 minutes to 59 minutes)*/
    public static void createAlarm(Context context, ArrayList<String> tags, int requestCode, int dayOfWeek, int hour, int minute)
    {
        //Create Calendar instance
        Calendar targetDate = Calendar.getInstance();
        //Set calendar to current time (let's us schedule the alarm for this week)
        targetDate.setTimeInMillis(System.currentTimeMillis());
        //Set day of week, hour of day, and minute of the hour
        targetDate.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        targetDate.set(Calendar.HOUR_OF_DAY, hour);
        targetDate.set(Calendar.MINUTE, minute);

        //We need the current time
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTimeInMillis(System.currentTimeMillis());

        /*Checks if the target time is before the current time
        * If we set such an alarm, android will fire it even if the time has passed
        * Therefore, we need to postpone it until next week
        * If the month is almost over, it will carry over to the next month
        * Praise the Crane*/
        if(targetDate.compareTo(currentDate) <= 0)
        {
            targetDate.set(Calendar.DAY_OF_MONTH, targetDate.get(Calendar.DAY_OF_MONTH) + 7);
        }

        //Get an AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //Intent to start broadcast receiver
        Intent intent = new Intent(context, ScheduledSearchReceiver.class);
        //Let's the broadcast receiver know it's being called
        intent.setAction(ACTION_TO_SCHEDULED_SEARCH_RECEIVER);
        //put as extra the requestCode (probably unnecessary)
        //intent.putExtra("rq", requestCode);
        //put the tags list as extra
        intent.putStringArrayListExtra("tags", tags);
        //Bundle bundle = new Bundle();
        //bundle.putStringArrayList("tags", tags);
        //intent.putExtras(bundle);

        /*This is given to the alarm
        * We wrap our intent with this
        * AlarmManager will call up the pending intent
        * and fire the intent inside
        * It's important to remember requestCode
        * We need to recreate this and the Intent above if we want to cancel them at a later date*/
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, requestCode,  intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //must specify flag so it can pass extras

        /*RTC_WAKEUP wakes up the device and fires the intent
        * targetDate tells it when to fire (if it has passed already, it will fire immediately)
        * Repeat weekly
        * Provide the pending intent
        * Alarm is scheduled*/
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                targetDate.getTimeInMillis(),
                alarmManager.INTERVAL_DAY*7,
                alarmIntent);
    }

    /**We need a context to get AlarmManager (pass the activity instance)
    * requestCode must be unique and have been stored in a file
    * If it's not the same requestCode, we won't be able to cancel the alarm*/
    public static void deleteAlarm(Context context, int requestCode)
    {
        /*We need the same intent to cancel an alarm.
        *
        * Remove any alarms with a matching Intent.
        * Any alarm, of any type, whose Intent matches this one
        * (as defined by filterEquals(Intent)), will be canceled.
        *
        * Determine if two intents are the same
        * for the purposes of intent resolution (filtering).
        * That is, if their action, data, type, class, and categories are the same.
        * This does not compare any extra data included in the intents.*/

        //Create the same way we did in createAlarm()
            //Intent to start broadcast receiver
            Intent intent = new Intent(context, ScheduledSearchReceiver.class);
            //Let's the broadcast receiver know it's being called
            intent.setAction(ACTION_TO_SCHEDULED_SEARCH_RECEIVER);
        //Create the same way we did in createAlarm()
            /*This is given to the alarm
            * We wrap our intent with this
            * AlarmManager will call up the pending intent
            * and fire the intent inside
            * It's important to remember requestCode
            * We need to recreate this and the Intent above if we want to cancel them at a later date*/
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, requestCode,  intent, 0);

        //Create AlarmManager instance, and delete the alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(alarmIntent);
    }

    /**Did that asshole edit the alarm?
    * Do you need to delete all those alarms spread across the week?
    * Just gimme a list of those alarms' requestCodes
    * Then you can forget them, and make new ones for the new alarms
        * We need a context to get AlarmManager (pass the activity instance)
        * requestCode must be unique and have been stored in a file
        * If it's not the same requestCode, we won't be able to cancel the alarm*/
    public static void deleteMultipleAlarms(Context context, ArrayList<Integer> requestCodes)
    {
        for (int x : requestCodes)
        {
            if(DEBUG) Log.e(TAG, "Deleting alarm. request code: " + x);

            deleteAlarm(context, x);
        }
    }

    /**Does that asshole want alarms on different days of the week?
    * Gimme a list of the days you want, and a list of those days' requestCodes
    * I'll make them for you
    * Make sure the days and their requestCodes are in the same index on the lists
        * We need a context to set the alarm (pass the activity instance)
        * Include the tags so we can include append them to the search terms
        * requestCode must be unique and stored in a file
        * Give dayOfWeek in the range 1 to 7 (Sunday to Saturday)
        * Give hour in military time in the range 0 to 23 (12 AM/midnight to 11 PM/before midnight)
        * Give minute in the range 0 to 59 (0 minutes to 59 minutes)*/
    public static void createMultipleAlarms(Context context, ArrayList<String> tags, ArrayList<Integer> requestCodes, ArrayList<Integer> daysOfTheWeek, int hour, int minute)
    {
        for (int x = 0; x < requestCodes.size(); x++)
        {
            if(DEBUG)
            {
                Log.e(TAG, "Creating alarm. request code: " + requestCodes.get(x));
            }

            createAlarm(context, tags, requestCodes.get(x), daysOfTheWeek.get(x), hour, minute);
        }
    }
}