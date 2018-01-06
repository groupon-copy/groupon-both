package com.example.marcus.grouponadministrator.Two;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.marcus.grouponadministrator.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Z_YelpResultsService extends Service
        implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    /*Unique identifier for this service; used in the log*/
    private static final String TAG = "Z_YelpResultsService";
    private static final boolean DEBUG = false;

    /*ActivityBroadcaster listens for this string
    * If this string is not fired with the intent
    * ActivityBroadcaster cannot be made active
    * ActivityBroadcaster must also have this listed as its action in the manifest*/
    private static final String ACTION_STRING_ACTIVITY = "YELP_RESULTS";

    /*Activity notifies whether or not it is in the foreground*/
    public static class ServiceBroadcaster extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(DEBUG) Log.i(TAG, "ServiceBroadcaster received message: activity in foreground = " + activityIsForeground);

            activityIsForeground = !activityIsForeground;
        }
    }

    public void setForeground()
    {
        if(DEBUG)  Log.i(TAG, "ServiceBroadcaster received message: activity in foreground = " + activityIsForeground);

        activityIsForeground = !activityIsForeground;

    }

    private static boolean activityIsForeground = false;

    /*Different ID per notification results in notification spam
    * Using the same ID everytime simply updates the notification*/
    int mID;

    /*Used to store location*/
    private Location mLastLocation;

    /*Google client to interact with Google API*/
    private GoogleApiClient mGoogleApiClient;

    /*Boolean flag to toggle periodic location updates*/
    private boolean mRequestingLocationUpdates = true;

    /*Used to set settings for location updates*/
    private LocationRequest mLocationRequest;

    /** These settings are used in createLocationRequest()
    * Set the lower and upper bounds for times to receive location updates
    * Using displacement updates the location
    * IFF the there is a change greater than the displacement*/
    private static int UPDATE_INTERVAL = 10*1000;//10 seconds
    private static int FASTEST_UPDATE_INTERVAL = 5*1000;//5 seconds

    /*GPS updates either via wifi or actual GPS from mobile service provider
    * In the intended case, user will be on the move outside of wifi sources
    * Therefore they must rely on mobile service to update location and query Yelp
    * To conserve data usage, use displacement.
    * GPS only updates for great enough changes.
    * Only then will data be used to query Yelp.*/
    private static int DISPLACEMENT = 100;//10 meters

    /*Stores the time in which the location was last updated*/
    private String mLastUpdateTime;

    /**Android doesn't have default values for 'enabling' permissions.
    * Instead, it allows the programmer to control how to react when permissions are denied.
    * Wherever premissions are need, these custom defined codes are thrown.
    * Handle it in onRequestPermissionsResult() in the main activity (services can't handle permission requests)
    * Any operations that require location services will check for the requisite permissions.
    * If the permissions are lacking, it will prompt the user to grant them, thereby throwing this code.*/
    private static int REQUEST_FINE_LOCATION = 0;

    /**The Service must create dialogs in order to prevent errors.
    * Services by definition are non-UI components.
    * Therefore they must make these dialogs through an activity.*/
    private Activity mBoundActivty;

    /*Used to bind an activity to a service*/
    private final IBinder mBinder = new LocalBinder();

    /*Returns instance of LocationSerice so that clients can call public methods*/
    public class LocalBinder extends Binder
    {
        Z_YelpResultsService getService()
        {
            if(DEBUG) Log.i(TAG, "Instance of Z_YelpResultsService returned.");

            return Z_YelpResultsService.this;
        }
    }

    @Nullable
    @Override
    /*Returns this Service's binder*/
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /*Initializations and starts locaiton updates*/
    public void start(Activity activity)
    {
        if(DEBUG) Log.i(TAG, "Starting Z_YelpResultsService");

        mBoundActivty = activity;
        if(checkGooglePlayServices())
        {
            buildGoogleApiClient();
            createLocationRequest();
            mGoogleApiClient.connect();
            startLocationUpdates();
        }
    }

    /*Called to stop location updates and to disconnect google api client*/
    public void stop()
    {
        stopLocationUpdates();

        if(mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();

        TagsCurrentlyBeingSearch.clearTagsFromFile(this);
    }

    /*Requests location updates from the FusedLocationApi.*/
    protected void startLocationUpdates()
    {
        /*Ensure google api client is connected and continuous updates is on*/
        if(mGoogleApiClient.isConnected() && mRequestingLocationUpdates)

            /*Check for requisite permissions*/
            /*If: Permissions are already available*/
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                if(DEBUG) Log.i(TAG, "Permissions were previously enabled; starting location updates.");

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
            else
            {
                if(DEBUG) Log.i(TAG, "Permissions were unavailable; prompting user to grant permissions.");

                /*If: do we need to explain to the user why we need it?*/
                if (ActivityCompat.shouldShowRequestPermissionRationale(mBoundActivty, Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    Toast.makeText(mBoundActivty, "We can't tell you about deals in your area without your GPS location.", Toast.LENGTH_LONG).show();

                    //Try to get permission again
                    //startLocationUpdates();
                }

                /*Request location services permission*/
                ActivityCompat.requestPermissions(mBoundActivty, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            }
    }

    /*Removes location updates from the FusedLocationApi.*/
    protected void stopLocationUpdates()
    {
        /*It is a good practice to remove location requests when the activity is in a paused or
        * stopped state. Doing so helps battery performance and is especially
        * recommended in applications that request frequent location updates.
        * The final argument to {@code requestLocationUpdates()} is a LocationListener
        * (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).*/
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /*Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.*/
    protected synchronized void buildGoogleApiClient()
    {
        if(DEBUG) Log.i(TAG, "Building GoogleApiClient");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest()
    {
        if(DEBUG) Log.i(TAG, "creating location request");

        mLocationRequest = new LocationRequest();

        /*
        * Sets the desired interval for active location updates.
        * The interval is inexact;
        * there may be no updates in no location sources are available;
        * updates may also be slower than desired.
        * If other applications are updating the location, updates may be faster.*/
        mLocationRequest.setInterval(UPDATE_INTERVAL);

        /**
        * Sets the fastest rate for location updates.
        * This interval is exact and location updates will never occur faster than this*/
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);


        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        /**
        * Displacement takes precedence over the intervals
        * If the change in location is less than the displacement
        * then there are no updates*/
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    @Override
    /*Called when GoogleApiClient successfully connects*/
    public void onConnected(@Nullable Bundle bundle)
    {
        if(DEBUG) Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.

        if (mRequestingLocationUpdates)
        {
            startLocationUpdates();
        }
    }

    @Override
    /*If GoogleApiClient connect is suspended,, try reconnecting.*/
    public void onConnectionSuspended(int i)
    {
        if(DEBUG) Log.i(TAG, "Connection suspended");

        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        mGoogleApiClient.connect();
    }

    @Override
    /*Recover gracefully*/
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        if(DEBUG) Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
    }

    /*Checks if Google Play Services are available*/
    private boolean checkGooglePlayServices()
    {
        GoogleApiAvailability gApi = GoogleApiAvailability.getInstance();
        int errorCode = gApi.isGooglePlayServicesAvailable(this);

        if(errorCode != ConnectionResult.SUCCESS)
        {
            if(DEBUG) Log.i(TAG, "check GP entered : error code != success");

            if(gApi.isUserResolvableError(errorCode))
            {
                if(DEBUG) Log.i(TAG, "GooglePlay unavailable; requesting that user log in.");

                //1000?, 9000? idk, static final int
                gApi.getErrorDialog(mBoundActivty, errorCode, 9000).show();
            }
            else
            {
                if(DEBUG) Log.i(TAG, "This device does not support GooglePlay.");

                Toast.makeText(mBoundActivty, "This device doesn't support Google Play Services; functionality is restricted", Toast.LENGTH_LONG).show();
                //finish();
            }
            return false;
        }
        if(DEBUG) Log.i(TAG, "GooglePlay available; continuing unobstructed.");

        return true;
    }

    /*do stuff*/
    public void sendNotification(String content)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("We found some stuff :)")
                        .setContentText(content)
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                        .setAutoCancel(true);

        //notification brings us back to this activity
        Intent resultIntent = new Intent(this, Z_YelpResultsActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainScreenActivity.class);
        //stackBuilder.addParentStack(MainFeedActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mID = 1000;

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mID, mBuilder.build());
        //using various mIDs created multiple notifications
        //calling the same mID on an existing notification updates its content
    }


    @Override
    public void onLocationChanged(Location location)
    {
        //if location updates again and we haven't finished
        reentrantLock1.lock();

        mLastLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        //pull search terms from file
        ArrayList<String> terms = TagsCurrentlyBeingSearch.getTagsFromFile(this);
        String out = "";
        for(String s : terms)
            out += s + " ";
        Log.e(TAG, "tags: " + out);

        setupYelpSearch(terms, mLastLocation.getLatitude(), mLastLocation.getLongitude());
    }

    private ReentrantLock reentrantLock1 = new ReentrantLock(true);
    private ReentrantLock reentrantLock2 = new ReentrantLock(true);
    private int num_terms;
    private ArrayList<Business> businesses;

    public void setupYelpSearch(ArrayList<String> terms, double latitude, double longitude)
    {
        //reset businesses list
        businesses = new ArrayList<>();

        //use this to determine when to send broadcast
        num_terms = terms.size();

        //make separate call for each term
        businesses = new ArrayList<>();
        for(String x : terms)
        {
            doYelpSearch(x, latitude, longitude);
        }
    }

    public void doYelpSearch(String search, double latitude, double longitude)
    {
        //do yelp stuff
        YelpAPIFactory apiFactory = new YelpAPIFactory(
                "f4GC0YHeZW-bOh6hDV-4JA",               //Consumer Key
                "o5YGHeHFOUUjCDw_davOA7UevhI",          //Consumer Secret
                "lzD_uwdQwDFoaKS02xkZuvIWoeuyC7vD",     //Token
                "GdaBB3unzKFr7angeE6YpCr1z0U");         //Token Secret
        YelpAPI yelpAPI = apiFactory.createAPI();

        Map<String, String> params = new HashMap<>();

        params.put("term", search);
        params.put("sort", "1");
        params.put("limit", "5");

        CoordinateOptions coordinate = CoordinateOptions.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();

        Call<SearchResponse> call = yelpAPI.search(coordinate, params);

        Callback<SearchResponse> callback = new Callback<SearchResponse>()
        {
            @Override
            public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
                //call dibs to preserve integrity of the list
                reentrantLock2.lock();

                businesses.addAll(response.body().businesses());

                //have we run through all the search terms?
                num_terms--;
                if (num_terms == 0)
                    sendBackToActivity();

                //relinquish access to the list
                reentrantLock2.lock();
            }

            @Override
            public void onFailure(Throwable t)
            {
                // HTTP error happened, do something to handle it.

                //call dibs to preserve integrity of the list
                reentrantLock2.lock();

                //have we run through all the search terms?
                num_terms--;
                if(num_terms == 0)
                    sendBackToActivity();

                //relinquish access to the list
                reentrantLock2.lock();
            }
        };

        call.enqueue(callback);
    }

    public void sendBackToActivity()
    {
        Intent intent = new Intent();
        intent.setAction(ACTION_STRING_ACTIVITY);
        intent.putExtra("busi", businesses);
        sendBroadcast(intent);

        /*User won't know important information
        * if the activity has been relegated to the background
        * Circumvent this by sending notifications
        * Custom-define them as desired*/
        if(!activityIsForeground)
        {
            //String coor = "(" + mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude() + ")";
            String notificationContent = businesses.get(0).name() + " : " + mLastUpdateTime;
            sendNotification(notificationContent);
        }

        //allow other threads to access setupYelpSearch
        reentrantLock1.unlock();
    }
}