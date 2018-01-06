package com.example.marcus.grouponadministrator.One;

import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.marcus.grouponadministrator.R;
import com.example.marcus.grouponadministrator.Utility;

public class MainFeedSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener
{
    public static final String mainFeedSearchActivity_TAGLIST = "tagList";

    DealListLayoutFragment mainFeedDealListLayoutFragment;
    SearchView searchView;

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
        setContentView(R.layout.one_activity_main_feed_search);

        //gets toolbar/action bar
        Toolbar toolbar = (Toolbar)findViewById(R.id.mainFeedSearchActivity_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar(); // Get a support ActionBar corresponding to this toolbar
        ab.setDisplayHomeAsUpEnabled(true); // Enable the Up button
        ab.setDisplayShowHomeEnabled(true);

        //get back arrow drawable
        /*final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        //set the color of back arrow
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        //use this arrow as the support action bar HomeAsUpIndicator button
        getSupportActionBar().setHomeAsUpIndicator(upArrow);*/

        String tagList = getIntent().getStringExtra(mainFeedSearchActivity_TAGLIST);

        //create dealListLayoutFragment
        mainFeedDealListLayoutFragment = DealListLayoutFragment.newInstance(tagList);
        //add fragment to activity
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mainFeedSearchActivity_LL, mainFeedDealListLayoutFragment)
                .commit();
    }

    //ToolBar menu method Interactions
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.one_main_feed_search_toolbar, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.mainFeedSearchToolbar_search).getActionView();
        if (null != searchView)
        {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }
        searchView.setOnQueryTextListener(this);

        searchView.setIconified(false);

        //show soft keyboard
        searchView.requestFocus();
        Utility.showSoftKeyboard(this, searchView);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.mainFeedSearchToolbar_mainScreen)
        {
            //Intent intent = new Intent(this, MainScreenActivity.class);
            //startActivity(intent);
            Toast.makeText(MainFeedSearchActivity.this, "goto main screen activity", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.mainFeedSearchToolbar_settings)
        {
            Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.mainFeedSearchToolbar_help)
        {
            Toast.makeText(this, "help", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void gotoMainScreenActivity(View view)
    {
        //Intent intent = new Intent(this, MainScreenActivity.class);
        //startActivity(intent);

        Toast.makeText(MainFeedSearchActivity.this, "goto main activity", Toast.LENGTH_SHORT).show();
    }

    //dealing with keyboard and search icon
    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent)
    {
        if(Utility.isPointerInsideEditTextOrSearchView(this, motionEvent))
        {
            //hides the soft keyboard
            Utility.hideSoftKeyboard(this);

            //remove focus, so user don't need to press back button twice to get back
            searchView.clearFocus();

            //collapse the search icon in Toolbar
            getSupportActionBar().collapseActionView(); //can't do it bc searchView has been set always show in menu layout
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

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        //Here u can get the value "query" which is entered in the search box.
        mainFeedDealListLayoutFragment.refreshDealListLoaderWithNewTagsList(query);

        //hides the soft keyboard
        Utility.hideSoftKeyboard(MainFeedSearchActivity.this);

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
}
