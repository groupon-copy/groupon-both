package com.example.marcus.grouponadministrator.One;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcus.grouponadministrator.Administrator.DealCreateActivity;
import com.example.marcus.grouponadministrator.Administrator.SendGrouponNotificationActivity;
import com.example.marcus.grouponadministrator.Two.MainScreenActivity;
import com.example.marcus.grouponadministrator.R;
import com.example.marcus.grouponadministrator.User.LoginRegisterActivity;
import com.example.marcus.grouponadministrator.User.User;
import com.example.marcus.grouponadministrator.User.PersonalizeAccountActivity;
import com.example.marcus.grouponadministrator.Utility;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainFeedActivity extends AppCompatActivity
{
    private static final String TAG = "MainFeedActivity";
    private static final boolean DEBUG = false;

    public static boolean userStateChanged = false;

    private MainFeedActivityNavigation navigationHandler;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle toggle;

    private String[] titles = {"food", "drink", "entertainment", "retail", "hotel", "bars"};
    private String[] tags = {"food", "drink", "entertainment", "retail", "hotel", "bars"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        if(DEBUG) Log.e(TAG, "onCreate");

        //set color of status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        setTitle(R.string.main_feed);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_activity_main_feed);

        //gets toolbar/action bar
        Toolbar toolbar = (Toolbar)findViewById(R.id.mainFeedActivity_toolbar);
        //set support action bar to the newly created toolbar
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar(); // Get a support ActionBar corresponding to this toolbar
        //ab.setElevation(0); //no elevation
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //get navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //create new handler for navigation view
        navigationHandler = new MainFeedActivityNavigation(this, navigationView);
        handleUserState();

        ViewPager viewPager = (ViewPager) findViewById(R.id.mainFeedActivity_viewPager);
        if (viewPager != null)
        {
            Adapter adapter = new Adapter(getSupportFragmentManager());
            for(int i = 0; i < tags.length; i++)
                adapter.addFragment(DealListLayoutFragment.newInstance(tags[i]), titles[i]);

            viewPager.setAdapter(adapter);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.mainFeedActivity_tabs);
        //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE); done in xml file
        tabLayout.setSelectedTabIndicatorHeight(0);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState)
    {
        super.onPostCreate(savedInstanceState, persistentState);

        //Call syncState() from your Activity's onPostCreate
        //to synchronize the indicator with the state of the linked
        //DrawerLayout after onRestoreInstanceState has occurred.
        toggle.syncState();
    }

    @Override
    protected void onResume()
    {
        if(userStateChanged) {
            handleUserState();
            userStateChanged = false;
        }

        super.onResume();
    }

    private void handleUserState() {
        if(User.isLoggedIn()) {
            //change navigation view content
            //pass the username and avatar image url
            navigationHandler.setAsLogin(User.getUser_username(), User.getUser_avatar_img_full_url());
        } else {
            navigationHandler.setAsNotLogin();
        }
    }

    static class Adapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title)
        {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position)
        {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    //ToolBar menu method Interactions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.one_main_feed_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;

        switch(item.getItemId())
        {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.mainFeedToolbar_mainScreen:
                //TODO to remove later
                Log.d("firebase token", FirebaseInstanceId.getInstance().getToken());
                break;
            case R.id.mainFeedToolbar_settings:
                intent = new Intent(this, SendGrouponNotificationActivity.class);
                startActivity(intent);
                break;
            case R.id.mainFeedToolbar_help:
                Toast.makeText(this, "help", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mainFeedToolbar_search:
                intent = new Intent(this, MainFeedSearchActivity.class);
                startActivity(intent);
                break;
            case R.id.mainFeedToolbar_createNewDeal:
                intent = new Intent(this, DealCreateActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void gotoMainScreenActivity(View view)
    {
        Intent intent = new Intent(this, MainScreenActivity.class);
        startActivity(intent);
    }

    //dealing with keyboard and search icon
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

        /*
        if(Utility.isSwipeLeftOrRight(motionEvent))
        {
            String txt = "Ad Panel/Fragment "; //+ Integer.toString(currentAdFragment) + " SWIPED";
            Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
        }
        */

        return super.dispatchTouchEvent(motionEvent);
    }
}
