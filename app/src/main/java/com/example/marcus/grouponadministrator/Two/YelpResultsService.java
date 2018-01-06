package com.example.marcus.grouponadministrator.Two;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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

import java.util.ArrayList;

/**
 * Created by Marcus Chiu on 4/30/2016.
 */
public class YelpResultsService implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        YelpAPI2.OnYelpAPI
        //Callback<SearchResponse>
{
    //Unique identifier for this service; used in the log
    private static final String TAG = "YelpResultsService";
    private static final boolean DEBUG = false;

    //activity that instantiated this Object
    private Activity activity;

    //Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    //Used to set settings for location updates
    private LocationRequest mLocationRequest;
    //See LocationAPI about usage
    private static int UPDATE_INTERVAL = 10*1000;//10 seconds
    private static int FASTEST_UPDATE_INTERVAL = 10*1000;//10 seconds
    private static int DISPLACEMENT = 100;//100 meters

    //Boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = true;

    private final int maxNumBusinessesInList = 30;
    private ArrayList<Business> businessArrayList;

    private YelpResultsNotification yelpResultsNotification2;

    private OnYelpResultsService3 mListener;
    private boolean isStarted;

    public YelpResultsService(Activity activity)
    {
        this.activity = activity;

        //instantiate variables
        businessArrayList = new ArrayList<>();

        //instantiate "Robust" Notification
        yelpResultsNotification2 = new YelpResultsNotification(activity);

        //set mListener Interface
        if (activity instanceof OnYelpResultsService3)
            mListener = (OnYelpResultsService3) activity;
        else
            throw new RuntimeException(activity.toString() + " must implement OnYelpResultsNotification");

        isStarted = false;
    }

    public void startYelpResultsService()
    {
        if(DEBUG) Log.i(TAG, "Starting");

        //connect to location services
        if(checkGooglePlayServices())
        {
            buildGoogleApiClient();
            mLocationRequest = LocationAPI.createLocationRequest(UPDATE_INTERVAL, FASTEST_UPDATE_INTERVAL, DISPLACEMENT);
            mGoogleApiClient.connect();
            startLocationUpdates();
        }

        //create Z_YelpResultsNotification
        yelpResultsNotification2.createNotification();
    }

    public void stopYelpResultsService()
    {
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
        businessArrayList = null;

        //destroy Z_YelpResultsNotification
        yelpResultsNotification2.destroyNotification();

        isStarted = false;
    }

    //Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
    private synchronized void buildGoogleApiClient()
    {
        if(DEBUG) Log.i(TAG, "Building GoogleApiClient");

        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //Requests location updates from the FusedLocationApi
    private void startLocationUpdates()
    {
        if(DEBUG) Log.i(TAG, "starting location updates");

        //Ensure google api client is connected and continuous updates is on
        if(mGoogleApiClient.isConnected() && mRequestingLocationUpdates)

            //Check for requisite permissions
            //If: Permissions are already available
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                if(DEBUG) Log.i(TAG, "Permissions were previously enabled; starting location updates.");

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
            else
            {
                if(DEBUG) Log.i(TAG, "Permissions were unavailable; prompting user to grant permissions.");

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
            isStarted = true;
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
        isStarted = false;
    }

    //Checks if Google Play Services are available
    private boolean checkGooglePlayServices()
    {
        GoogleApiAvailability gApi = GoogleApiAvailability.getInstance();
        int errorCode = gApi.isGooglePlayServicesAvailable(activity);

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
        if(DEBUG) Log.i(TAG, "onLocationChange");

        //pull search terms from file
        ArrayList<String> terms = TagsCurrentlyBeingSearch.getTagsFromFile(activity);

        if(DEBUG)
        {
            String out = "";
            for(String s : terms)
                out += s + " ";
            Log.i(TAG, "current tags: " + out);
        }

        int returnSize = 20;
        //pass this, (OnYelpAPIResponse) to be able to callback
        //terms, to search for
        //location, where you currently are
        //returnSize, returns businesses of max size
        //businessArrayList, to exclude already existing businesses on display
        YelpAPI2.callYelp(this, terms, location, returnSize, businessArrayList);
    }

    public boolean isStarted()
    {
        return isStarted;
    }

    @Override
    public void OnYelpAPIResults(ArrayList<Business> newBusinesses)
    {
        //update business list
        //new businesses will be near the front, while old businesses are appended at the back
        //list returned is cut off at specified number
        businessArrayList = YelpAPI.getUpdatedBusinessList(newBusinesses, businessArrayList, maxNumBusinessesInList);

        yelpResultsNotification2.updateImage(businessArrayList.get(0).imageUrl(), businessArrayList.get(0).name(), businessArrayList.get(0).snippetText());

        //send business array list to activity
        mListener.OnYelpResponse(businessArrayList);
    }

    //interaction with activity
    public interface OnYelpResultsService3
    {
        void OnYelpResponse(ArrayList<Business> businesses);
    }
}
