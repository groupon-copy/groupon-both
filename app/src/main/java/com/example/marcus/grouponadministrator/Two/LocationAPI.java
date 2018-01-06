package com.example.marcus.grouponadministrator.Two;

import com.google.android.gms.location.LocationRequest;

/**
 * Created by Marcus Chiu on 4/26/2016.
 */
public class LocationAPI
{
    /**Android doesn't have default values for 'enabling' permissions.
     * Instead, it allows the programmer to control how to react when permissions are denied.
     * Wherever permissions are need, these custom defined codes are thrown.
     * Handle it in onRequestPermissionsResult() in the main activity (services can't handle permission requests)
     * Any operations that require location services will check for the requisite permissions.
     * If the permissions are lacking, it will prompt the user to grant them, thereby throwing this code.*/
    public static final int REQUEST_FINE_LOCATION = 0;

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
    public static LocationRequest createLocationRequest(int updateInterval, int fastestUpdateInterval, int displacement)
    {
        LocationRequest mLocationRequest = new LocationRequest();

        //Sets the desired interval for active location updates.
        //The interval is inexact;
        //there may be no updates in no location sources are available;
        //updates may also be slower than desired.
        //If other applications are updating the location, updates may be faster.
        mLocationRequest.setInterval(updateInterval);

        //Sets the fastest rate for location updates.
        //This interval is exact and location updates will never occur faster than this
        mLocationRequest.setFastestInterval(fastestUpdateInterval);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //Displacement takes precedence over the intervals
        //If the change in location is less than the displacement
        //then there are no updates
        mLocationRequest.setSmallestDisplacement(displacement);

        return mLocationRequest;
    }
}
