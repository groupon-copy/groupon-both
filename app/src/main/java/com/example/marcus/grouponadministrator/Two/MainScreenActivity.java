package com.example.marcus.grouponadministrator.Two;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;

public class MainScreenActivity extends AppCompatActivity implements
        MainScreenTimeFrameEditFragment.OnMainScreenTimeFrameEditFragmentInteractionListener,
        LoaderManager.LoaderCallbacks<TimeFrameEditList>
{
    private static final String TAG = "MainScreenActivity";
    private static final boolean DEBUG = false;

    public MainScreenTimeFrameLayoutFragment mainScreenTimeFrameLayoutFragment;
    public static TimeFrameEditList timeFrameEditList;
    private static final int LOADER_ID = 0;

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
        setContentView(R.layout.two_activity_main_screen);

        setTitle(R.string.main_screen);

        //set tool bar
        Toolbar toolbar = (Toolbar)findViewById(R.id.mainScreenActivity_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar(); // Get a support ActionBar corresponding to this toolbar
        ab.setDisplayHomeAsUpEnabled(true); // Enable the Up button

        //createTimeFrameLayoutFragment();
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        timeFrameEditList.writeTimeFrameEditListToFile(this, "time_frame_edit_list");
    }

    @Override
    protected void onDestroy()
    {
        if(DEBUG) Log.e(TAG, "OnDestroy");

        super.onDestroy();
    }

    private void createTimeFrameLayoutFragment()
    {
        //create fragment
        mainScreenTimeFrameLayoutFragment = new MainScreenTimeFrameLayoutFragment();

        //add fragment to layout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mainScreenActivity_timeframeLL, mainScreenTimeFrameLayoutFragment)
                .commit(); //instead of commit() because of bug
        //http://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform-this-action-after-onsaveinstancestate-wit
    }

    //ToolBar functions
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.two_main_screen_toolbar, menu);
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
        if (id == R.id.mainScreenToolbar_mainFeed)
        {
            //Intent intent = new Intent(this, MainFeedActivity.class);
            //startActivity(intent);
            return true;
        }
        if (id == R.id.mainScreenToolbar_settings)
        {
            Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.mainScreenToolbar_help)
        {
            Toast.makeText(this, "help", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.mainScreenToolbar_search)
        {
            Intent intent = new Intent(this, YelpSearchActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void addNewEdit(View view)
    {
        mainScreenTimeFrameLayoutFragment.addNewTimeframeEdit();
    }

    public void findNow(View view)
    {
        Intent intent = new Intent(this, YelpResultsActivity.class);
        //TODO
        ArrayList<String> tags = timeFrameEditList.getTagArrayListofAllOnTimeFrameEdits();
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("startNow", true);
        intent.putStringArrayListExtra("tags", tags);
        startActivity(intent);
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

    //Timeframe Edit Fragment Interactions
    @Override
    public void onMainScreenTimeFrameEditFragmentInteractionDelete(int index)
    {
        mainScreenTimeFrameLayoutFragment.deleteTimeframeEditFragment(index);
    }
    @Override
    public void onMainScreenTimeFrameEditFragmentInteractionClicked(int index)
    {
        mainScreenTimeFrameLayoutFragment.gotoTimeframeEditActivity(index);
    }
    @Override
    public void onMainScreenTimeFrameEditFragmentInteractionONOFF(int index, boolean state)
    {
        mainScreenTimeFrameLayoutFragment.setTimeframeEditFragmentONOFF(index, state);
    }

    @Override
    public Loader<TimeFrameEditList> onCreateLoader(int id, Bundle args)
    {
        return new TimeFrameEditListLoader(this);
    }
    @Override
    public void onLoadFinished(Loader<TimeFrameEditList> loader, TimeFrameEditList data)
    {
        if(data == null)
        {
            Log.e(TAG, "data is null");
        }
        else
        {
            Log.e(TAG, "data is NOT null");
            timeFrameEditList = data;
            createTimeFrameLayoutFragment();
        }

    }
    @Override
    public void onLoaderReset(Loader<TimeFrameEditList> loader)
    {

    }
}
