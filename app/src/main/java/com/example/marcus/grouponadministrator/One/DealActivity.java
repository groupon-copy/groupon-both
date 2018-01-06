package com.example.marcus.grouponadministrator.One;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcus.grouponadministrator.Config.AdminDatabaseVariable;
import com.example.marcus.grouponadministrator.Administrator.DealUpdateActivity;
import com.example.marcus.grouponadministrator.Config.DatabaseVariable;
import com.example.marcus.grouponadministrator.Database.PostUrlLoader;
import com.example.marcus.grouponadministrator.MapFragment;
import com.example.marcus.grouponadministrator.R;
import com.example.marcus.grouponadministrator.Database.Deal;
import com.example.marcus.grouponadministrator.Database.Vendor;
import com.example.marcus.grouponadministrator.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class DealActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<String>, MapFragment.OnMapFragmentInteractionListener
{
    public static Deal deal;
    public static Drawable image;

    // The loader's unique id. Loader ids are specific to the Activity or Fragment in which they reside.
    private static final int LOADER_DELETE_ID = 3;
    private static final int LOADER_VENDOR_ID = 2;
    private Vendor vendor;

    CollapsingToolbarLayout collapsingToolbarLayout;
    DealContentFragment dealContentFragment;

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
        setContentView(R.layout.one_activity_deal);

        //setTitle(R.string.deal);
        //setTitle(""); //similar to collapsingToolbarLayout setTitle()

        //gets toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.dealActivity_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar(); // Get a support ActionBar corresponding to this toolbar
        ab.setDisplayHomeAsUpEnabled(true); // Enable the Up button

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(""); //similar to setTitle()
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        //collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        //collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.dealActivity_fab);
        fab.setOnClickListener(this);
        //fab.setRippleColor(lightVibrantColor);
        //fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));

        if(deal == null)
        {
            //default
            //TODO go back to previous screen
        }
        else
        {
            //TODO gets called again when crashes or rebuilt
            setDealData(deal);

            if(vendor == null && dealContentFragment == null)
            {
                // Prepare the loader.  Either re-connect with an existing one, or start a new one.
                getLoaderManager().initLoader(LOADER_VENDOR_ID, null, this);
            }
        }
    }

    private void setDealData(Deal deal)
    {
        //set image backdrop
        ImageView imageView = (ImageView)findViewById(R.id.dealActivity_imageView);
        if(image == null)
            imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.no_image));
        else
        {
            Drawable clone = image.getConstantState().newDrawable();
            imageView.setImageDrawable(clone);
        }

        TextView validUntilTextView = (TextView)findViewById(R.id.dealActivity_timeRemainingTextView);
        validUntilTextView.setText(deal.getValidUntil());

        Button buyNowButton = (Button)findViewById(R.id.dealActivity_buyNowButton);
        buyNowButton.setOnClickListener(this);
    }

    private void setVendorData(Vendor vendor)
    {
        if(vendor != null)
        {
            //set toolbar title to vendor name
            collapsingToolbarLayout.setTitle(vendor.getName());

            //create deal content fragment
            dealContentFragment = DealContentFragment.newInstance(deal, vendor);

            //add fragment to activity
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.dealActivity_nestedScrollView, dealContentFragment)
                    .commit();
        }
        else
        {
            collapsingToolbarLayout.setTitle("Vendor is NULL");

            //TODO display something
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.one_deal_toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Bundle bundle = new Bundle();

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId())
        {
            case R.id.dealToolbar_settings:
                Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
                break;

            case R.id.dealToolbar_help:
                Toast.makeText(this, "help", Toast.LENGTH_SHORT).show();
                break;

            case R.id.dealToolbar_update:
                Intent intent = new Intent(this, DealUpdateActivity.class);

                //pass deal to next activity
                bundle.putSerializable(DealUpdateActivity.ARG_DEAL, deal);
                intent.putExtras(bundle);

                startActivity(intent);
                break;

            case R.id.dealToolbar_delete:
                getLoaderManager().initLoader(LOADER_DELETE_ID, null, this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view)
    {
        if(vendor == null) return;

        int id = view.getId();

        switch(id)
        {
            case R.id.dealActivity_fab:
                gotoVendorPage();
                break;

            case R.id.dealActivity_buyNowButton:
                Toast.makeText(DealActivity.this, "You Lost Some Money!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void gotoVendorPage()
    {
        if(vendor != null)
        {
            Intent intent = new Intent(this, VendorPageActivity.class);
            VendorPageActivity.vendor = vendor;
            startActivity(intent);
        }
    }

    @Override
    public void onMapFragmentClicked()
    {
        Utility.gotoAddress(this, vendor.getFullAddress());
    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        try {
            if(i == LOADER_DELETE_ID) {
                HashMap<String, String> params = new HashMap<>();
                params.put(DatabaseVariable.ID, Integer.toString(deal.getID()));
                return new PostUrlLoader(getApplicationContext(), new URL(AdminDatabaseVariable.DELETE_DEAL_URL), params);
            } else if(i == LOADER_VENDOR_ID) {
                HashMap<String, String> params = new HashMap<>();
                params.put(DatabaseVariable.ID, Integer.toString(deal.getVendorID()));
                return new PostUrlLoader(this, new URL(DatabaseVariable.GET_VENDOR_BY_ID_URL), params);
            }
        } catch(MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        try {
            if(loader.getId() == LOADER_DELETE_ID) {
                JSONObject jo = new JSONObject(data);
                Toast.makeText(DealActivity.this, jo.getString("message"), Toast.LENGTH_SHORT).show();

                //go back to main feed screen
                Intent intent = new Intent(DealActivity.this, MainFeedActivity.class);
                startActivity(intent);
            } else if(loader.getId() == LOADER_VENDOR_ID) {
                JSONObject jo = new JSONObject(data);

                //if no error
                if(!jo.getBoolean(DatabaseVariable.ERROR)) {
                    JSONArray ja =  jo.getJSONArray(DatabaseVariable.VENDOR_RETURN_STRING);
                    vendor = new Vendor(ja.getJSONObject(0));

                    setVendorData(vendor);
                } else {
                    Toast.makeText(DealActivity.this, jo.getString(DatabaseVariable.MESSAGE), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        if(loader.getId() == LOADER_DELETE_ID) {
            Toast.makeText(DealActivity.this, "DeleteLoader has been reset", Toast.LENGTH_SHORT).show();
        } else if(loader.getId() == LOADER_VENDOR_ID) {
            Toast.makeText(DealActivity.this, "LoadVendor has been reset", Toast.LENGTH_SHORT).show();
        }
    }
}
