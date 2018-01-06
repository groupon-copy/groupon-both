package com.example.marcus.grouponadministrator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

/**
 * Created by Marcus Chiu on 3/19/2016.
 */
public class Utility
{
    //min swipe distance
    static final int MIN_DISTANCE = 150;
    static float x1 = 0;
    static float x2;

    // Hides the soft keyboard
    public static void hideSoftKeyboard(Activity activity)
    {
        if(activity.getCurrentFocus()!=null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    //Shows the soft keyboard
    public static void showSoftKeyboard(Context context, View view)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    public static boolean isPointerInsideEditTextOrSearchView(Activity activity, MotionEvent motionEvent)
    {
        //returns the current focused view
        //not the same as the coordinates of pointer
        View currentFocusView = activity.getCurrentFocus();

        if(currentFocusView instanceof EditText || currentFocusView instanceof SearchView)
        {
            //get coordinates of pointer
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();

            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && !getRectOnScreen(currentFocusView).contains(x, y))
            {
                return true;
            }
        }

        return false;
    }

    public static boolean isSwipeLeftOrRight(MotionEvent motionEvent)
    {
        switch(motionEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = motionEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = motionEvent.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    return true;
                }
                else
                {
                    // consider as something else - a screen tap for example
                    //return false;
                }
                break;
        }
        return false;
    }

    private static Rect getRectOnScreen(View view)
    {
        Rect mRect = new Rect();
        int[] location = new int[2];

        view.getLocationOnScreen(location);

        mRect.left = location[0];
        mRect.top = location[1];
        mRect.right = location[0] + view.getWidth();
        mRect.bottom = location[1] + view.getHeight();

        return mRect;
    }

    public static String militaryTimeToNiceLookingTime(int hour, int minute)
    {
        String hourS = "" + hour;
        String minutesS = "" + minute;
        String AMorPM = "PM";

        if(hour < 12)
        {
            AMorPM = "AM";

            if(hour == 0) hourS = "12";
        }
        if(hour > 12) hourS = "" + (hour - 12);
        if(minutesS.length() == 1)
        {
            minutesS = "0" + minutesS;
        }
        return hourS + ":" + minutesS + " " + AMorPM;
    }

    public static void gotoAddress(Context context, String address)
    {
        if(address == null) return;

        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + address);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    public static void call(Context context, String phoneNumber)
    {
        if(phoneNumber == null) return;

        try
        {
            context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
        }

        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
        }
    }

    public static void gotoWebsite(Context context, String website)
    {
        if(website == null) return;

        Uri uri = Uri.parse("http://" + website);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static void setStatusBarColor(Activity activity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {

            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
        }
    }

    public static Bitmap getLargeIcon(Context context)
    {
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.ghoomo_icon_small);
    }

    /*
    Note that having an active network interface doesn't guarantee that a particular networked service is available. Network issues,
    server downtime, low signal, captive portals, content filters and the like can all prevent your app from reaching a server.
    For instance you can't tell for sure if your app can reach Twitter until you receive a valid response from the Twitter service.
    must have permissions in Manifest
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     */
    public static boolean isConnected2Internet(Activity activity)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean  isConnecting2Internet(Activity activity)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /*
    //not used because connectivityManager.getNetworkInfo is deprecated
    public static boolean isConnected2Internet(Context context)
    {
        // get Connectivity Manager object to check connection
        ConnectivityManager connectivityManager =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connectivityManager.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connectivityManager.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connectivityManager.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED )
        {
            return true;
        }
        else if (connectivityManager.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                connectivityManager.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  )
        {
            return false;
        }

        return false;
    }
    */
}
