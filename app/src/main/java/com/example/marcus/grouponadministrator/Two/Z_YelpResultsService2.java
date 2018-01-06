package com.example.marcus.grouponadministrator.Two;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Marcus Chiu on 4/26/2016.
 */
public class Z_YelpResultsService2 extends Service
        implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Callback<SearchResponse>
        //LoaderManager.LoaderCallbacks<Bitmap>
        /*,
        Z_YelpResultsNotification.OnYelpResultsNotification*/
{
    //Unique identifier for this service; used in the log
    private static final String TAG = "Z_YelpResultsService2";
    private static final boolean DEBUG = true;

    private static final String TO_YELP_RESULTS_ACTIVITY = "YELP_RESULTS";

    //Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    //Used to set settings for location updates
    private LocationRequest mLocationRequest;
    //See LocationAPI about usage
    private static int UPDATE_INTERVAL = 10*1000;//10 seconds
    private static int FASTEST_UPDATE_INTERVAL = 5*1000;//5 seconds
    private static int DISPLACEMENT = 100;//100 meters

    //Boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = true;

    private int LOADER_ID = 1;
    private ArrayList<String> tags;
    private final int maxNumBusinessesInList = 30;
    private ArrayList<Business> businessArrayList;

    //Used to bind an activity to a service
    private final IBinder mBinder = new LocalBinder();

    //Returns instance of this Service, so that clients can call public methods
    public class LocalBinder extends Binder
    {
        Z_YelpResultsService2 getService()
        {
            if(DEBUG) Log.i(TAG, "Instance of Z_YelpResultsService2 returned.");

            return Z_YelpResultsService2.this;
        }
    }

    @Nullable
    @Override
    //Returns this Service's binder to system
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    @Override
    public void onCreate()
    {
        if(DEBUG) Log.e(TAG, "OnCreate");

        super.onCreate();

        //instantiate variables
        businessArrayList = new ArrayList<>();
        tags = new ArrayList<>();

        //connect to location services
        if(checkGooglePlayServices())
        {
            buildGoogleApiClient();
            mLocationRequest = LocationAPI.createLocationRequest(UPDATE_INTERVAL, FASTEST_UPDATE_INTERVAL, DISPLACEMENT);
            mGoogleApiClient.connect();
            startLocationUpdates();
        }

        //create notification
        //Z_YelpResultsNotification.createNotification(this);
    }

    @Override //gets called whenever startService is called on this Service
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(DEBUG) Log.e(TAG, "OnStartCommand");

        //get tags from intent
        ArrayList<String> tags = intent.getStringArrayListExtra("tags");

        if(tags != null)
        {
            //add tags to list ONLY if they don't currently exist
            for(String newTag : tags)
            {
                for(String tag: this.tags)
                {
                    if(!newTag.equalsIgnoreCase(tag))
                        this.tags.add(newTag);
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        if(DEBUG) Log.e(TAG, "OnDestroy");

        /*Stop Location Updates
        * It is a good practice to remove location requests when the activity is in a paused or
        * stopped state. Doing so helps battery performance and is especially
        * recommended in applications that request frequent location updates.
        * The final argument to {@code requestLocationUpdates()} is a LocationListener
        * (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).*/
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        //disconnect from googleAPI
        if(mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();

        //clear stuff
        tags = null;
        businessArrayList = null;

        //destroy Z_YelpResultsNotification
        //Z_YelpResultsNotification.destroyNotification();

        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        if(DEBUG) Log.e(TAG, "OnTaskRemoved");

        stopSelf();

        super.onTaskRemoved(rootIntent);
    }

    //Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
    protected synchronized void buildGoogleApiClient()
    {
        if(DEBUG) Log.i(TAG, "Building GoogleApiClient");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //Requests location updates from the FusedLocationApi
    protected void startLocationUpdates()
    {
        if(DEBUG) Log.e(TAG, "starting location updates");

        //Ensure google api client is connected and continuous updates is on
        if(mGoogleApiClient.isConnected() && mRequestingLocationUpdates)

            //Check for requisite permissions
            //If: Permissions are already available
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                if(DEBUG) Log.e(TAG, "Permissions were previously enabled; starting location updates.");

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
            else
            {
                if(DEBUG) Log.e(TAG, "Permissions were unavailable; prompting user to grant permissions.");

                //If: do we need to explain to the user why we need it?
                /*if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    Toast.makeText(mBoundActivity, "We can't tell you about deals in your area without your GPS location.", Toast.LENGTH_LONG).show();

                    //Try to get permission again
                    //startLocationUpdates();
                }

                //Request location services permission
                ActivityCompat.requestPermissions(mBoundActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LocationAPI.REQUEST_FINE_LOCATION);*/
            }
    }

    //GoogleAPIClient Interface
    @Override
    //Called when GoogleApiClient successfully connects
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
    //If GoogleApiClient connect is suspended, try reconnecting.
    public void onConnectionSuspended(int i)
    {
        if(DEBUG) Log.i(TAG, "Connection suspended");

        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        mGoogleApiClient.connect();
    }
    @Override
    //Recover gracefully
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        if(DEBUG) Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
    }

    //Checks if Google Play Services are available
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
            }
            else
            {
                if(DEBUG) Log.i(TAG, "This device does not support GooglePlay.");
            }
            return false;
        }
        if(DEBUG) Log.i(TAG, "GooglePlay available; continuing unobstructed.");

        return true;
    }

    //LocationListener Interface
    @Override
    public void onLocationChanged(Location location)
    {
        if(DEBUG) Log.e(TAG, "onLocationChange");

        //pull search terms from file
        ArrayList<String> terms = TagsCurrentlyBeingSearch.getTagsFromFile(this);

        if(DEBUG)
        {
            String out = "";
            for(String s : terms)
                out += s + " ";
            Log.e(TAG, "tags: " + out);
        }

        //TODO \\YelpAPI2.setupYelpSearch(terms, mLastLocation.getLatitude(), mLastLocation.getLongitude());

        for(String s: terms)
        {
            ArrayList<String> toYelp = new ArrayList<>();
            toYelp.add(s);
            YelpAPI.callYelp(toYelp, location, this);
        }
    }

    @Override
    public void onResponse(Response<SearchResponse> response, Retrofit retrofit)
    {
        if(DEBUG) Log.e(TAG, "YELP onResponse started");

        //Here is the search results with parsed Java objects
        SearchResponse searchResponse = response.body();
        ArrayList<Business> newBusinessArrayList = searchResponse.businesses();

        //update business list
        //new businesses will be near the front, while old businesses are appended at the back
        //list returned is cut off at specified number
        businessArrayList = YelpAPI.getUpdatedBusinessList(newBusinessArrayList, businessArrayList, maxNumBusinessesInList);

        //TODO
        //load image URL of first business, and update Z_YelpResultsActivity
//TODO        //Z_YelpResultsActivity.loaderManager.initLoader(LOADER_ID, null, this);

        //send business array list to activity
        Intent intent = new Intent(TO_YELP_RESULTS_ACTIVITY);
        intent.setAction(TO_YELP_RESULTS_ACTIVITY);
        intent.putExtra("busi", businessArrayList);
        sendBroadcast(intent);
    }
    @Override
    public void onFailure(Throwable t)
    {
        if(DEBUG) Log.e(TAG, "YELP onFailure");
    }

    //ImageLoader Interface
    /*@Override
    public Loader<Bitmap> onCreateLoader(int id, Bundle args)
    {
        return new ImageLoader(this, businessArrayList.get(0).imageUrl());
    }
    @Override
    public void onLoadFinished(Loader<Bitmap> loader, Bitmap data)
    {
        Z_YelpResultsNotification.updateImage(this, data, businessArrayList.get(0).name(), businessArrayList.get(0).snippetText());
    }
    @Override
    public void onLoaderReset(Loader<Bitmap> loader)
    {

    }*/
}
