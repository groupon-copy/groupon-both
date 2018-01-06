package com.example.marcus.grouponadministrator.One;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcus.grouponadministrator.R;
import com.example.marcus.grouponadministrator.Database.Vendor;
import com.example.marcus.grouponadministrator.Database.VendorHours;
import com.example.marcus.grouponadministrator.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VendorPageActivity extends AppCompatActivity
        implements VendorPageReviewFragment.OnReviewFragmentInteractionListener,
        View.OnClickListener
{
    public static Vendor vendor;

    private boolean reviewsCreated = false;
    private boolean hoursDisplayed = false;

    CollapsingToolbarLayout collapsingToolbarLayout;

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
        setContentView(R.layout.one_activity_vendor_page);

        Toolbar toolbar = (Toolbar)findViewById(R.id.vendorPageActivity_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar(); // Get a support ActionBar corresponding to this toolbar
        ab.setDisplayHomeAsUpEnabled(true); // Enable the Up button

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(""); //similar to setTitle()
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        //collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        //collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));

        //FAB not used
        //FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        //fab.setOnClickListener(this);

        if(vendor == null)
        {
            //default
        }
        else
        {
            setData(vendor);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.one_vendor_page_tool_bar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.vendorPageToolbar_settings)
        {
            Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.vendorPageToolbar_help)
        {
            Toast.makeText(this, "help", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setData(Vendor vendor)
    {
        //set Toolbar Title
        setTitle(vendor.getName());

        TextView vendorName = (TextView)findViewById(R.id.vendorPageActivity_vendorNameTextView);
        vendorName.setText(vendor.getName());

        TextView rating = (TextView)findViewById(R.id.vendorPageActivity_ratingNumber);
        rating.setText(Double.toString(vendor.getFiveStarRating()));
        RatingBar ratingBar = (RatingBar)findViewById(R.id.vendorPageActivity_ratingBar);
        ratingBar.setRating((float) vendor.getFiveStarRating());
        TextView pricing = (TextView)findViewById(R.id.vendorPageActivity_pricingTextView);
        pricing.setText(vendor.getPriceRange());

        ImageView imageView = (ImageView)findViewById(R.id.vendorPageActivity_imageView);
        //load image externally into ImageView TODO can tweak resize
        Picasso.with(this)
                .load(vendor.getImageFullURL())
                //.centerCrop() //for some reason it crashes without resize()
                //.resize(500,500)
                .placeholder(R.drawable.ghoomo)
                .error(R.drawable.no_image)
                .into(imageView);

        LinearLayout phoneLayout = (LinearLayout)findViewById(R.id.vendorPageActivity_phoneLayout);
        phoneLayout.setOnClickListener(this);

        LinearLayout addressLayout = (LinearLayout)findViewById(R.id.vendorPageActivity_addressLayout);
        addressLayout.setOnClickListener(this);

        LinearLayout websiteLayout = (LinearLayout)findViewById(R.id.vendorPageActivity_websiteLayout);
        websiteLayout.setOnClickListener(this);

        //Description
        TextView description = (TextView)findViewById(R.id.vendorPageActivity_descriptionTextView);
        description.setText(vendor.getDescription());


        LinearLayout addressLayout2 = (LinearLayout)findViewById(R.id.vendorPageActivity_addressLayout2);
        addressLayout2.setOnClickListener(this);
        TextView addressTextView = (TextView)findViewById(R.id.vendorPageActivity_addressTextView);
        addressTextView.setText(vendor.getFullAddress());

        LinearLayout hoursLayout = (LinearLayout)findViewById(R.id.vendorPageActivity_hoursLayout);
        hoursLayout.setOnClickListener(this);
        TextView openOrClose = (TextView)findViewById(R.id.vendorPageActivity_hourOpenOrClosedTextView);
        int result = vendor.getIsOpenDetails();
        if(result == VendorHours.OPEN_TODAY)
        {
            openOrClose.setText("Open Today");
        }
        else if(result == VendorHours.CLOSED_TODAY)
        {
            openOrClose.setText("Closed Today");
            openOrClose.setTextColor(Color.parseColor("#FF0000"));
        }
        else if(result == VendorHours.TIME_VARIES)
        {
            openOrClose.setText("Time Varies");
            openOrClose.setTextColor(Color.parseColor("#FFE600"));
        }

        LinearLayout menuLayout = (LinearLayout)findViewById(R.id.vendorPageActivity_menuLayout);
        menuLayout.setOnClickListener(this);

        LinearLayout phoneLayout2 = (LinearLayout)findViewById(R.id.vendorPageActivity_phoneLayout2);
        phoneLayout2.setOnClickListener(this);
        TextView phoneTextView = (TextView)findViewById(R.id.vendorPageActivity_phoneNumberTextView);
        phoneTextView.setText(vendor.getPhone1());

        LinearLayout websiteLayout2 = (LinearLayout)findViewById(R.id.vendorPageActivity_websiteLayout2);
        websiteLayout2.setOnClickListener(this);
        TextView websiteTextView = (TextView)findViewById(R.id.vendorPageActivity_websiteTextView);
        websiteTextView.setText(vendor.getVendorWebsite());
    }

    private void createReviewsLayout()
    {
        TextView showReviews = (TextView)findViewById(R.id.vendorPageActivity_showReviewsTextView);
        showReviews.setText("Reviews");

        for(int i = 0; i < 10; i++)
        {
            //create a new Reviews Fragment
            VendorPageReviewFragment frag = new VendorPageReviewFragment();
            frag.setReviewNum(i);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.vendorPageActivity_ReviewsLL, frag)
                    .commit();
        }
    }

    @Override
    public void onReviewFragmentInteraction(int reviewNum)
    {
        String txt = "Review " + Integer.toString(reviewNum) + " clicked";
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    }

    private void toggleDisplayHours()
    {
        if(vendor.getHours() == null)
        {
            //TODO display that hours are not specified
            Toast.makeText(VendorPageActivity.this, "hours not specified", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!hoursDisplayed)
        {
            String[] dayStrings = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            List<VendorHours.VendorDay> days = vendor.getHours().days;

            for(int i = 0; i < 7; i++)
            {
                //create a new Fragment
                VendorPageHoursIndividualFragment frag = VendorPageHoursIndividualFragment.newInstance(days.get(i),dayStrings[i]);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.vendorPageActivity_hoursLayoutBlank, frag)
                        .commit();
            }

            hoursDisplayed = true;
        }
        else
        {
            LinearLayout ll = (LinearLayout)findViewById(R.id.vendorPageActivity_hoursLayoutBlank);
            ll.removeAllViews();
            hoursDisplayed = false;
        }
    }

    public void showReviews(View view)
    {
        if(!reviewsCreated)
        {
            createReviewsLayout();
            reviewsCreated = true;
        }
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

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        switch(id)
        {
            case R.id.vendorPageActivity_phoneLayout:
                Utility.call(VendorPageActivity.this, vendor.getPhone1());
                break;

            case R.id.vendorPageActivity_addressLayout:
                Utility.gotoAddress(VendorPageActivity.this, vendor.getFullAddress());
                break;

            case R.id.vendorPageActivity_websiteLayout:
                Utility.gotoWebsite(VendorPageActivity.this, vendor.getVendorWebsite());
                break;

            case R.id.vendorPageActivity_addressLayout2:
                Utility.gotoAddress(VendorPageActivity.this, vendor.getFullAddress());
                break;

            case R.id.vendorPageActivity_phoneLayout2:
                Utility.call(VendorPageActivity.this, vendor.getPhone1());
                break;

            case R.id.vendorPageActivity_menuLayout:
                Toast.makeText(VendorPageActivity.this, "menu activity in progress", Toast.LENGTH_SHORT).show();
                break;

            case R.id.vendorPageActivity_hoursLayout:
                toggleDisplayHours();
                break;

            case R.id.vendorPageActivity_websiteLayout2:
                Utility.gotoWebsite(VendorPageActivity.this, vendor.getVendorWebsite());
                break;
        }
    }
}
