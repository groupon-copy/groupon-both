package com.example.marcus.grouponadministrator.Two;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
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
public class Z_YelpResultsActivity extends AppCompatActivity
        implements YelpBusinessListViewLayoutFragment.OnYelpBusinessListViewLayoutFragmentInteractionListener,
        Z_YelpResultsNotification.OnYelpResultsNotification
{
    private static final String TAG = "Z_YelpResultsActivity";
    private static final boolean DEBUG = true;

    private DialogFilter filterDialog;
    private DialogCurrentTags dialogCurrentTags;

    private static ArrayList<Business> businessArrayList;
    private static YelpBusinessListViewLayoutFragment yelpBusinessListViewLayoutFragment;

    private FloatingActionButton fab;

    public static android.app.LoaderManager loaderManager;

    //mService will call methods within the service
    //mBound tells us whether the connect to the service is live
    //With a boolean check, we can avoid using a null service and avoid errors
    Z_YelpResultsService2 mService;
    public static boolean mBound = false;
    private ServiceConnection mConnection = new ServiceConnection()
    {
        @Override
        //Called when a connect to the service has been established
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            if(DEBUG) Log.i(TAG, "service connected");

            Z_YelpResultsService2.LocalBinder binder = (Z_YelpResultsService2.LocalBinder) service;
            mService = binder.getService();
            mBound = true;


            //There is lag until mService is given an object
            // Using mService before then will cause NullPointer Exceptions
            //This is probably because bindService creates a new thread to start the service
            //Therefore, calls to the service's methods should be done here
            //and not within the activity lifecycle methods
            //Excluding of course the calls in onDestroy() where we must destroy the service
            if(mService != null)
                Log.e(TAG, "mService is not null, can call public methods");
            else
                Toast.makeText(Z_YelpResultsActivity.this, "mService is null", Toast.LENGTH_LONG).show();
        }

        @Override
        //alled when connection to the service is lost.
        //The ServiceConnection object is NOT lost.
        //The object remains active
        //When the service is once again available, onServiceConnected() is called.
        public void onServiceDisconnected(ComponentName name)
        {
            mBound = false;
        }
    };

    //A non-static receiver won't work
    public static class YelpResultsReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(DEBUG) Log.i(TAG, "YelpResultsReceiver received message");

            //if fragment not null display new businesses
            if(yelpBusinessListViewLayoutFragment != null)
            {
                if(DEBUG) Log.i(TAG, "YelpResultsReceiver fragment not null");

                businessArrayList = (ArrayList<Business>) intent.getSerializableExtra("busi");

                if(DEBUG && businessArrayList != null)
                {
                    if(DEBUG) Log.i(TAG, "YelpResultsReceiver newBusinessArrayList not null");
                }

                //update business layout with new business list
                yelpBusinessListViewLayoutFragment.updateBusinessListLayout(businessArrayList);

                //TODO moved to service
                //update yelp results notification
                //Drawable drawable = ResourcesCompat.getDrawable(resources, R.drawable.sushi, null);
                //Z_YelpResultsNotification.updateImage(context, null, businessArrayList.get(0).name(), businessArrayList.get(0).snippetText());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //TODO
        loaderManager = getLoaderManager();

        if(DEBUG) Log.e(TAG, "onCreate");

        //set color of status bar
        Utility.setStatusBarColor(this);

        //create activity layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_activity_yelp_results);

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

        //get Intent
        Intent intent = getIntent();
        int requestCode = intent.getIntExtra("requestCode", -1);
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

            //connect to Z_YelpResultsService
            connectYelpResultsService();
        }
        //else user navigated to this activity through application
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
        if(DEBUG) Log.e(TAG, "onNewIntent");

        int requestCode = intent.getIntExtra("requestCode", -1);
        //if sent by a notification, user navigated to this activity through notification
        if(requestCode != -1)
        {
            //TODO prompt user if want to clear tags or append tags to file

            //if user is already on the activity but have not started the service
            //this will be called
            if(!mBound)
            {
                connectYelpResultsService();
            }
            else
            {
                //test if needed
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
    protected void onResume()
    {
        if(DEBUG) Log.e(TAG, "onResume");

        super.onResume();

        //update businesses GUI layout
        if(yelpBusinessListViewLayoutFragment != null)
            yelpBusinessListViewLayoutFragment.updateBusinessListLayout(businessArrayList);
    }

    @Override
    protected void onDestroy()
    {
        if(DEBUG) Log.e(TAG, "onDestroy");

        super.onDestroy();

        //set to null so when user navigates away from activity while it receives a broadcast it won't crash
        yelpBusinessListViewLayoutFragment = null;

        //Always unbind the service to prevent memory leaks
        disconnectYelpResultsService();
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
        if(DEBUG) Log.e(TAG, "connectingYelpResultsService");

        //check if fine location permission is available
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if(DEBUG) Log.e(TAG, "Permissions were previously enabled; starting location updates.");

            //Bound services will live from the moment of this declaration to the moment the activity is destroyed
            //Services in general will run in the background even if the summoning activity is not in the foreground
            //even if the screen is locked Services are still subject to battery conservation constraints
            //Android OS reduces CPU activity to conserve battery while the screen is locked
            //Therefore the service will be started, stopped, and restarted on the OS's own schedule
            //If the service provides important real-time information, then this is bad
            //This can be overcome with a partial wakelock declared within the service
            Intent intent = new Intent(this, Z_YelpResultsService2.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

            //create notification
            Z_YelpResultsNotification.createNotification(this);

            //change icon
            fab.setImageResource(R.drawable.ic_pause_black_24dp);
        }
        //else request fine location permission
        else
        {
            requestFineLocationPermission();
        }
    }
    private void disconnectYelpResultsService()
    {
        //Always unbind the service to prevent memory leaks
        //Call in onDestroy() ensures the service is destroyed reliably
        if(mBound)
        {
            mService.stopSelf();
            unbindService(mConnection);
            mBound = false;

            //destroy notification
            Z_YelpResultsNotification.destroyNotification();

            //change icon
            fab.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
    }

    public void fabClicked(View view)
    {
        if(mBound)
            disconnectYelpResultsService();
        else
            connectYelpResultsService();
    }

    @Override
    public void onYelpBusinessListViewLayoutFragmentInteractionRefresh()
    {
        Toast.makeText(Z_YelpResultsActivity.this, "refresh?", Toast.LENGTH_SHORT).show();
        yelpBusinessListViewLayoutFragment.updateBusinessListLayout(businessArrayList);
    }

    private void requestFineLocationPermission()
    {
        if(DEBUG) Log.e(TAG, "requesting fine location permission");

        //check if fine location permission is NOT available
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //If: do we need to explain to the user why we need it?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                if(DEBUG) Log.e(TAG, "We can't tell you about deals in your area without your GPS location.");
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
        if(DEBUG) Log.e(TAG, "onRequestPermissionResult");

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
        Intent intent = new Intent(this, Z_YelpResultsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}