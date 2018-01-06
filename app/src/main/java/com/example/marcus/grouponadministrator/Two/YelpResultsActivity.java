package com.example.marcus.grouponadministrator.Two;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.marcus.grouponadministrator.R;
import com.example.marcus.grouponadministrator.Utility;
import com.yelp.clientlib.entities.Business;

import java.util.ArrayList;

/**
 * Demonstration of Z_YelpResultsService
 * Anything that must be added to the activity manually is indicated with @ADD
 * Use search to find all of them; copy paste :)*/
public class YelpResultsActivity extends AppCompatActivity
        implements
        YelpBusinessListViewLayoutFragment.OnYelpBusinessListViewLayoutFragmentInteractionListener,
        YelpResultsNotification.OnYelpResultsNotification,
        YelpResultsService.OnYelpResultsService3
{
    private static final String TAG = "YelpResultsActivity";
    private static final boolean DEBUG = true;

    private DialogFilter filterDialog;
    private DialogCurrentTags dialogCurrentTags;

    private ArrayList<Business> businessArrayList;
    private static YelpBusinessListViewLayoutFragment yelpBusinessListViewLayoutFragment;

    private FloatingActionButton fab;

    private YelpResultsService yelpResultsService3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        if(DEBUG) Log.i(TAG, "onCreate");

        //set color of status bar
        Utility.setStatusBarColor(this);

        //create activity layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_activity_yelp_results);


        //instantiate business array list
        businessArrayList = new ArrayList<>();

        //create yelpBusinessListViewLayoutFragment
        yelpBusinessListViewLayoutFragment = YelpBusinessListViewLayoutFragment.newInstance();
        //add fragment to activity
        getSupportFragmentManager().beginTransaction()
                .add(R.id.yelpResultsActivity_LL, yelpBusinessListViewLayoutFragment)
                .commit();

        //get floating action button
        fab = (FloatingActionButton) findViewById(R.id.yelpResultsActivity_fab);

        //get toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.yelpResultsActivity_toolbar);
        //set toolbar as support action toolbar
        setSupportActionBar(toolbar);
        //set back button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setElevation(0f);

        //instantiate yelpResults class
        if(yelpResultsService3 == null)
        {
            yelpResultsService3 = new YelpResultsService(this);
            Log.i(TAG, "creating new yelpService");
        }
        else
        {
            Log.e(TAG, "yelpService is not null");
        }

        //get Intent
        Intent intent = getIntent();
        int requestCode = intent.getIntExtra("requestCode", -1);
        Log.e(TAG, "requestCode received: " + requestCode);
        boolean startNow = intent.getBooleanExtra("startNow", false);
        //if sent by a notification, user navigated to this activity through notification
        if(requestCode != -1)
        {
            //TODO prompt user if want to clear tags file
            //for now clear file
            TagsCurrentlyBeingSearch.clearTagsFromFile(this);

            //get tags and add to file
            ArrayList<String> tags = intent.getStringArrayListExtra("tags");
            if(tags != null) TagsCurrentlyBeingSearch.addTagsToFile(this, tags);

            //cancel notification that spawned this activity
            Intent iDismiss = new Intent(ScheduledSearchReceiver.DISMISS);
            iDismiss.putExtra("requestCode", requestCode);
            sendBroadcast(iDismiss);

            //connect to YelpResultsService
            connectYelpResultsService();
        }
        //else user navigated to this activity through application
        else if(startNow)
        {
            //for now clear file, because user pressed "Start Now"
            TagsCurrentlyBeingSearch.clearTagsFromFile(this);

            //get tags and add to file
            ArrayList<String> tags = intent.getStringArrayListExtra("tags");
            if(tags != null) TagsCurrentlyBeingSearch.addTagsToFile(this, tags);

            //connect to YelpResultsService
            connectYelpResultsService();
        }
        else
        {
            //cannot be called twice at the same time
            //connectYelpResultsService above will call requestFineLocationPermission
            //so separate it, so they will not be called together
            requestFineLocationPermission();
        }

        //must be before starting FileService
        filterDialog = new DialogFilter(this);
        dialogCurrentTags = new DialogCurrentTags(this);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        if(DEBUG) Log.i(TAG, "onNewIntent");

        int requestCode = intent.getIntExtra("requestCode", -1);
        Log.e(TAG, "requestCode received: " + requestCode);
        //if sent by a notification, user navigated to this activity through notification
        if(requestCode != -1)
        {
            //TODO prompt user if want to clear tags or append tags to file

            //if user is already on the activity but have not started the service
            //this will be called
            if(!yelpResultsService3.isStarted())
            {
                connectYelpResultsService();
            }
            else
            {
                //TODO test if needed
                fab.setImageResource(R.drawable.ic_pause_black_24dp);
            }

            //get tags and add to file
            ArrayList<String> tags = intent.getStringArrayListExtra("tags");
            if(tags != null) TagsCurrentlyBeingSearch.addTagsToFile(this, tags);

            //cancel notification that spawned this activity
            Intent iDismiss = new Intent(ScheduledSearchReceiver.DISMISS);
            iDismiss.putExtra("requestCode", requestCode);
            sendBroadcast(iDismiss);
        }
        //user is already on this activity, and pressed on the "show list" action of YelpResults notification
        else
        {
            if(DEBUG) Log.e(TAG, "onNewIntent: requestCode is -1");
        }

        super.onNewIntent(intent);
    }

    @Override
    protected void onStart()
    {
        if(DEBUG) Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        if(DEBUG) Log.i(TAG, "onResume");

        super.onResume();

        //update businesses GUI layout
        if(yelpBusinessListViewLayoutFragment != null)
            yelpBusinessListViewLayoutFragment.updateBusinessListLayout(businessArrayList);
    }

    @Override
    protected void onPause()
    {
        if(DEBUG) Log.i(TAG, "onPause");

        super.onPause();
    }

    @Override
    protected void onStop()
    {
        if(DEBUG) Log.i(TAG, "onStop");
        super.onStop();
    }

    //swiping task does not always call onDestroy
    @Override
    protected void onDestroy()
    {
        if(DEBUG) Log.i(TAG, "onDestroy");

        //set to null so when user navigates away from activity while it receives a broadcast it won't crash
        yelpBusinessListViewLayoutFragment = null;

        //Always unbind the service to prevent memory leaks
        disconnectYelpResultsService();

        super.onDestroy();
    }

    public void showFilterDialog(View view)
    {
        filterDialog.show();
    }
    public void showDialogCurrentTags(View view)
    {
        dialogCurrentTags.show();
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.two_yelp_results_toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.yelpResultsToolbar_settings)
        {
            Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.yelpResultsToolbar_help)
        {
            Toast.makeText(this, "help", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.yelpResultsToolbar_search)
        {
            Intent intent = new Intent(this, YelpSearchActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent)
    {
        if(Utility.isPointerInsideEditTextOrSearchView(this, motionEvent))
        {
            //hides the soft keyboard
            Utility.hideSoftKeyboard(this);

            //collapse the search icon in Toolbar
            getSupportActionBar().collapseActionView();
        }

        return super.dispatchTouchEvent(motionEvent);
    }

    private void connectYelpResultsService()
    {
        if(DEBUG) Log.i(TAG, "connectingYelpResultsService");

        if(!yelpResultsService3.isStarted())
        {
            //check if fine location permission is available
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                if(DEBUG) Log.i(TAG, "Permissions were previously enabled; starting location updates.");

                yelpResultsService3.startYelpResultsService();

                //change icon
                fab.setImageResource(R.drawable.ic_pause_black_24dp);
            }
            //else request fine location permission
            else
            {
                requestFineLocationPermission();
            }
        }
    }
    private void disconnectYelpResultsService()
    {
        if(DEBUG) Log.i(TAG, "disconnectingYelpResultsService");

        //Always unbind the service to prevent memory leaks
        //Call in onDestroy() ensures the service is destroyed reliably
        if(yelpResultsService3.isStarted())
        {
            yelpResultsService3.stopYelpResultsService();

            //change icon
            fab.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
    }

    public void fabClicked(View view)
    {
        if(yelpResultsService3.isStarted())
            disconnectYelpResultsService();
        else
            connectYelpResultsService();
    }

    @Override
    public void onYelpBusinessListViewLayoutFragmentInteractionRefresh()
    {
        Toast.makeText(YelpResultsActivity.this, "refresh?", Toast.LENGTH_SHORT).show();
        yelpBusinessListViewLayoutFragment.updateBusinessListLayout(businessArrayList);
    }

    private void requestFineLocationPermission()
    {
        if(DEBUG) Log.i(TAG, "requesting fine location permission");

        //check if fine location permission is NOT available
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //If: do we need to explain to the user why we need it?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                if(DEBUG) Log.i(TAG, "We can't tell you about deals in your area without your GPS location.");
                Toast.makeText(this, "We can't tell you about deals in your area without your GPS location.", Toast.LENGTH_LONG).show();
            }

            //Request location services permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LocationAPI.REQUEST_FINE_LOCATION);
        }
    }
    //Necessary to get permission for location services
    //Needs special attention @RecoverGracefully
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(DEBUG) Log.i(TAG, "onRequestPermissionResult");

        //We requested access to GPS
        if(requestCode == LocationAPI.REQUEST_FINE_LOCATION)
        {
            //If: user grants permission, continue like normal
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();

                //mService.startLocationUpdates();
            }
            //Else: permission was denied
            else
            {
                //@RecoverGracefully
                //Disable the feature; recover gracefully (don't let the app break!); let the user know the folly of their folly
                Toast.makeText(this, "Permission was not granted", Toast.LENGTH_LONG).show();
            }
        }
        //The permission wasn't meant for this app, send to super
        else
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //Notification Interface
    @Override
    public void OnNotificationExit()
    {
        disconnectYelpResultsService();
    }
    @Override
    public void OnNotificationClicked()
    {

    }
    @Override
    public void OnNotificationList()
    {
        Log.i(TAG, "OnNotificationList");

        Intent intent = new Intent(this, YelpResultsActivity.class);
        /*intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Set the action and category so it appears that the app is being launched
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);*/
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    //YelpResults Interface
    @Override
    public void OnYelpResponse(ArrayList<Business> businesses)
    {
        businessArrayList = businesses;

        if(DEBUG && businessArrayList != null)
        {
            if(DEBUG) Log.i(TAG, "OnYelpResponse newBusinessArrayList not null");
        }

        //update business layout with new business list
        yelpBusinessListViewLayoutFragment.updateBusinessListLayout(businessArrayList);
    }
}
