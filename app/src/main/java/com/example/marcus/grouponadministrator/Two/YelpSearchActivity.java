package com.example.marcus.grouponadministrator.Two;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.marcus.grouponadministrator.R;
import com.example.marcus.grouponadministrator.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class YelpSearchActivity extends AppCompatActivity
        implements
        SearchView.OnQueryTextListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        Callback<SearchResponse>,
        YelpBusinessListViewLayoutFragment.OnYelpBusinessListViewLayoutFragmentInteractionListener,
        DialogOrderBy.OnDialogOrderBy
{
    private static final String TAG = "YelpSearchActivity";
    private static final boolean DEBUG = false;

    private ArrayList<String> terms;

    private DialogFilter filterDialog;
    private DialogOrderBy orderByDialog;

    private SearchView searchView;
    private YelpBusinessListViewLayoutFragment yelpBusinessListViewLayoutFragment;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //set color of status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_activity_yelp_search);

        //gets toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.yelpSearchActivity_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        //ab.setDisplayHomeAsUpEnabled(true);
        ab.setElevation(0f);

        if (mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        filterDialog = new DialogFilter(this);
        orderByDialog = new DialogOrderBy(this);

        //create yelpBusinessListViewLayoutFragment
        ArrayList<String> terms = getIntent().getStringArrayListExtra("terms");
        yelpBusinessListViewLayoutFragment = YelpBusinessListViewLayoutFragment.newInstance();
        //add fragment to activity
        getSupportFragmentManager().beginTransaction()
                .add(R.id.yelpSearchActivity_LL, yelpBusinessListViewLayoutFragment)
                .commit();
    }

    @Override
    protected void onStart()
    {
        mGoogleApiClient.connect();
        super.onStart();
    }
    @Override
    protected void onStop()
    {
        if(DEBUG) Log.e(TAG, "onStop");

        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(DEBUG) Log.e(TAG, "onDestroy");

        yelpBusinessListViewLayoutFragment = null;
    }

    public void showFilterDialog(View view)
    {
        filterDialog.show();
    }
    public void showOrderByDialog(View view)
    {
        orderByDialog.show();
    }

    //Toolbar Interface
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.two_yelp_search_toolbar, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.yelpSearchToolbar_search).getActionView();
        if (null != searchView)
        {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }

        //TODO expand search view automatically
        //searchView.setIconified(true);

        //show soft keyboard
        //searchView.requestFocus();
        //Utility.showSoftKeyboard(this, searchView);

        searchView.setOnQueryTextListener(this);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.yelpSearchToolbar_settings)
        {
            Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.yelpSearchToolbar_help)
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

    //SearchView OnQueryTextChangeListener Interface
    @Override
    public boolean onQueryTextSubmit(String query)
    {
        //Here u can get the value "query" which is entered in the search box.
        ArrayList<String> terms = new ArrayList<>();
        terms.add(query);

        Location location = getLocation();
        this.terms = terms;
        YelpAPI.callYelp(this.terms, location, this);

        //hides the soft keyboard
        Utility.hideSoftKeyboard(YelpSearchActivity.this);

        searchView.clearFocus();

        //collapse the search icon in Toolbar
        getSupportActionBar().collapseActionView();

        return true;
    }
    @Override
    public boolean onQueryTextChange(String newText)
    {
        return false;
    }

    private Location getLocation()
    {
        //Ensure google api client is connected and continuous updates is on
        if(mGoogleApiClient.isConnected())
        {
            //Check for requisite permissions
            //If: Permissions are already available
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                if(DEBUG) Log.i(TAG, "Permissions were previously enabled; starting location updates.");

                return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
            else
            {
                if(DEBUG) Log.i(TAG, "Permissions were unavailable; prompting user to grant permissions.");

                //If: do we need to explain to the user why we need it?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    Toast.makeText(this, "We can't tell you about deals in your area without your GPS location.", Toast.LENGTH_LONG).show();

                    //Try to get permission again
                    //startLocationUpdates();
                }

                //Request location services permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LocationAPI.REQUEST_FINE_LOCATION);
            }
        }
        else
        {
            if(DEBUG) Log.i(TAG, "Permissions were previously enabled; starting location updates.");
        }

        return null;
    }

    //GoogleApiClient.ConnectionCallbacks Interface
    @Override
    public void onConnected(Bundle bundle)
    {
        if(DEBUG) Log.i(TAG, "GogleApiClient connection success");

        //Ensure google api client is connected and continuous updates is on
        if(mGoogleApiClient.isConnected())
        {
            //Check for requisite permissions
            //If: Permissions are already available
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                if(DEBUG) Log.i(TAG, "Permissions were previously enabled; starting location updates.");
            }
            else
            {
                if(DEBUG) Log.i(TAG, "Permissions were unavailable; prompting user to grant permissions.");

                //If: do we need to explain to the user why we need it?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    Toast.makeText(this, "We can't tell you about deals in your area without your GPS location.", Toast.LENGTH_LONG).show();

                    //Try to get permission again
                    //startLocationUpdates();
                }

                //Request location services permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LocationAPI.REQUEST_FINE_LOCATION);
            }
        }
        else
        {
            if(DEBUG) Log.i(TAG, "Permissions were previously enabled; starting location updates.");
        }
    }
    @Override
    public void onConnectionSuspended(int i)
    {
        if(DEBUG) Log.i(TAG, "GogleApiClient connection suspended");
    }

    //GoogleApiClient.OnConnectionFailedListener Interface
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        if(DEBUG) Log.i(TAG, "GogleApiClient connection failed");
    }

    //SwipeRefreshLayout.OnRefreshListener Interface
    @Override
    public void onResponse(Response<SearchResponse> response, Retrofit retrofit)
    {
        if(DEBUG) Log.e(TAG, "onResponse started");

        //Here is the search results with parsed Java objects
        SearchResponse searchResponse = response.body();
        ArrayList<Business> businessList = searchResponse.businesses();

        if(yelpBusinessListViewLayoutFragment != null)
            yelpBusinessListViewLayoutFragment.updateBusinessListLayout(businessList);

        if(DEBUG) Log.e(TAG, "onResponse ended");
    }
    @Override
    public void onFailure(Throwable t)
    {
        yelpBusinessListViewLayoutFragment.updateBusinessListLayout(null);
    }

    //YelpBusinessListViewLayoutFragment Interface
    @Override
    public void onYelpBusinessListViewLayoutFragmentInteractionRefresh()
    {
        Location location = getLocation();
        YelpAPI.callYelp(terms, location, this);
    }

    @Override
    public void onDialogOrderByStop(int orderBySelectedIndex)
    {
        yelpBusinessListViewLayoutFragment.sortBusinessList(orderBySelectedIndex);
    }
}