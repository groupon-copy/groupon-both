package com.example.marcus.grouponadministrator.Two;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcus.grouponadministrator.R;
import com.example.marcus.grouponadministrator.Utility;

import java.util.ArrayList;
import java.util.List;

public class TimeFrameEditActivity extends AppCompatActivity implements View.OnClickListener, DialogTimeFrameEditTag.OnDialogTimeFrameEditTag
{
    private Toolbar toolbar;

    //TimeframeEdit Name
    private EditText timeframeEditName;
    //Fragment Alarm stuff
    private TimeFrameEditAlarmFragment alarmFragment;
    //where to put specified tag names
    private LinearLayout tagsLinearLayout;

    private int timeframeEditNumber;

    private DialogTimeFrameEditTag dialogTimeFrameEditTag;

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
        setContentView(R.layout.two_activity_timeframe_edit);

        setTitle(R.string.time_frame_edit);

        //gets toolbar
        toolbar = (Toolbar)findViewById(R.id.timeframeEditActivity_toolbar);
        setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        //get timeframe edit number
        //if none was sent return -1
        Intent intent = getIntent();
        timeframeEditNumber = intent.getIntExtra(MainScreenTimeFrameLayoutFragment.mainScreenActivity_timeframeEditNum,
                MainScreenTimeFrameLayoutFragment.mainScreenActivity_defaultTimeframeEditNum);

        //create a dialog for later use when user wants to add a tag
        dialogTimeFrameEditTag = new DialogTimeFrameEditTag(this);

        if(timeframeEditNumber < 0 || timeframeEditNumber >= MainScreenActivity.timeFrameEditList.getSize())
        {
            //default values
            boolean[] days = new boolean[]{false, false, false, false, false, false, false};
            createAlarmFragment(0,0,days);

            connectViews("", null);
        }
        else
        {
            createAlarmFragment(MainScreenActivity.timeFrameEditList.getStartHour(timeframeEditNumber),
                    MainScreenActivity.timeFrameEditList.getStartMinute(timeframeEditNumber),
                    MainScreenActivity.timeFrameEditList.getDaysOn(timeframeEditNumber));

            connectViews(MainScreenActivity.timeFrameEditList.getName(timeframeEditNumber),
                    MainScreenActivity.timeFrameEditList.getTagList(timeframeEditNumber));
        }
    }

    private void createAlarmFragment(int startHour, int startMinute, boolean[] days)
    {
        //create a new alarm fragment
        alarmFragment = TimeFrameEditAlarmFragment.newInstance(startHour, startMinute, days);

        //add the newly created
        getSupportFragmentManager().beginTransaction()
                .add(R.id.timeframeEditActivity_alarmPlaceHolder, alarmFragment)
                .commit();
    }

    private void connectViews(String name, List<String> searchEditNames)
    {
        timeframeEditName = (EditText)findViewById(R.id.timeframeEditActivity_timeframeName);
        timeframeEditName.setText(name);

        TextView searchEditAdd = (TextView)findViewById(R.id.timeframeEditActivity_searchEditAdd);
        searchEditAdd.setOnClickListener(this);

        //must go before addSearchEdit
        tagsLinearLayout = (LinearLayout)findViewById(R.id.timeframeEditActivity_searchEditLL);

        if(searchEditNames != null)
        {
            for(String str: searchEditNames)
            {
                addTagName(str);
            }
        }

        Button back = (Button)findViewById(R.id.timeframeEditActivity_backButton);
        back.setOnClickListener(this);
    }

    //ToolBar Methods
    //creates the Toolbar menu
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.two_time_frame_edit_toolbar, menu);
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
        if (id == R.id.timeFrameEditToolbar_mainScreen)
        {
            Intent intent = new Intent(this, MainScreenActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.timeFrameEditToolbar_settings)
        {
            Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.timeFrameEditToolbar_help)
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

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if(id == R.id.timeframeEditActivity_searchEditAdd)
        {
            //show dialog
            dialogTimeFrameEditTag.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialogTimeFrameEditTag.show();
        }
        else if(id == R.id.timeframeEditActivity_backButton)
        {
            //goes back to previous screen
            finish();
        }
        else if(v.getTag() == "tagName")
        {
            ((ViewManager)v.getParent()).removeView(v);
        }
    }

    private void addTagName(String str)
    {
        //create a new Category Button
        Button tagName = new Button(this);
        tagName.setTag("tagName");
        tagName.setOnClickListener(this);
        tagName.setText(str);

        //add the newly created Button
        tagsLinearLayout.addView(tagName);
    }

    @Override
    protected void onPause()
    {
        if(timeframeEditNumber != -1)
        {
            //get timeframe edit values from activity
            String name = timeframeEditName.getText().toString();

            int startHour = alarmFragment.getHourStart();
            int startMinute = alarmFragment.getMinuteStart();

            boolean[] days = alarmFragment.getDays();

            ArrayList<String> tags = new ArrayList<>();
            for(int i = 0; i < tagsLinearLayout.getChildCount(); i++)
            {
                View v = tagsLinearLayout.getChildAt(i);
                String tag = ((Button)v).getText().toString();

                tags.add(tag);
            }

            boolean ONOFF = MainScreenActivity.timeFrameEditList.getONorOFF(timeframeEditNumber);

            //update TimeFrame Edit, which also updates the alarms
            MainScreenActivity.timeFrameEditList.updateTimeframeEdit(this, timeframeEditNumber, name, startHour, startMinute, days, tags, ONOFF);
        }

        super.onPause();
    }

    @Override
    public void onDialogTimeFrameEditTagOkay(String text)
    {
        addTagName(text);
    }
}
