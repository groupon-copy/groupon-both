package com.example.marcus.grouponadministrator.Two;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.marcus.grouponadministrator.R;
import com.example.marcus.grouponadministrator.Utility;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeFrameEditAlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeFrameEditAlarmFragment extends Fragment implements View.OnClickListener
{
    //parameter arguments, choose names that match
    private static final String ARG_HOUR_START = "hourStart";
    private static final String ARG_MINUTE_START = "minuteStart";
    private static final String ARG_DAYS_BOOLEAN_ARRAY = "days";

    //types of parameters
    private int hourStart;
    private int minuteStart;
    private boolean[] days;

    //variables used within class
    private TextView startTimeTextView;

    private int textColorWhenUnClicked = Color.parseColor("#FFFFFFFF");
    private Button[] dayButtons = null;

    //Dialog Variables
    private Dialog dialog;
    private TimePicker timePicker;

    public TimeFrameEditAlarmFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
    */
    public static TimeFrameEditAlarmFragment newInstance(int hourStart, int minuteStart, boolean[] days)
    {
        TimeFrameEditAlarmFragment fragment = new TimeFrameEditAlarmFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_HOUR_START, hourStart);
        args.putInt(ARG_MINUTE_START, minuteStart);
        args.putBooleanArray(ARG_DAYS_BOOLEAN_ARRAY, days);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            hourStart = getArguments().getInt(ARG_HOUR_START);
            minuteStart = getArguments().getInt(ARG_MINUTE_START);
            days = getArguments().getBooleanArray(ARG_DAYS_BOOLEAN_ARRAY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.two_fragment_timeframe_edit_alarm, container, false);

        startTimeTextView = (TextView)view.findViewById(R.id.timeframeEditAlarmActivity_timeStartTextView);
        startTimeTextView.setOnClickListener(this);

        Button sun = (Button)view.findViewById(R.id.timeframeEditAlarmActivity_sunday);
        sun.setOnClickListener(this);
        Button mon = (Button)view.findViewById(R.id.timeframeEditAlarmActivity_monday);
        mon.setOnClickListener(this);
        Button tue = (Button)view.findViewById(R.id.timeframeEditAlarmActivity_tuesday);
        tue.setOnClickListener(this);
        Button wed = (Button)view.findViewById(R.id.timeframeEditAlarmActivity_wednesday);
        wed.setOnClickListener(this);
        Button thu = (Button)view.findViewById(R.id.timeframeEditAlarmActivity_thursday);
        thu.setOnClickListener(this);
        Button fri = (Button)view.findViewById(R.id.timeframeEditAlarmActivity_friday);
        fri.setOnClickListener(this);
        Button sat = (Button)view.findViewById(R.id.timeframeEditAlarmActivity_saturday);
        sat.setOnClickListener(this);
        dayButtons = new Button[]{sun, mon, tue, wed, thu, fri, sat};

        setTimesAndDayButtons(days, dayButtons);

        return view;
    }

    //called from onCreateView()
    private void setTimesAndDayButtons(boolean[] days, Button[] daysButton)
    {
        startTimeTextView.setText(Utility.militaryTimeToNiceLookingTime(hourStart, minuteStart));

        //set day button
        for(int i = 0; i < days.length; i++)
        {
            if(days[i])
            {
                daysButton[i].setBackgroundResource(R.drawable.rounded_button);
                daysButton[i].setTextColor(textColorWhenUnClicked);
            }
            else
            {
                daysButton[i].setBackgroundResource(R.drawable.rounded_button_inv);
                daysButton[i].setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            }
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }
    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if(id == R.id.timeframeEditAlarmActivity_timeStartTextView)
        {
            showDialog(hourStart, minuteStart);
        }
        else if(dayClicked(v))
        {
            //a day button was clicked
        }
        else if(id == R.id.dialogTimePicker_cancelButton)
        {
            dialog.dismiss();
        }
        else if(id == R.id.dialogTimePicker_OKButton)
        {
            //TODO getHour() works for marsh-mellow but not older versions
            int hour = timePicker.getCurrentHour();//getHour()
            int minute = timePicker.getCurrentMinute();//getMinute()
            startTimeTextView.setText(Utility.militaryTimeToNiceLookingTime(hour, minute));

            hourStart = hour;
            minuteStart = minute;

            dialog.dismiss();
        }
    }

    //called from onClick, returns true if view is a day button, false otherwise
    public boolean dayClicked(View view)
    {
        int id = view.getId();

        if(id == R.id.timeframeEditAlarmActivity_sunday)
        {
            toggleDayButton(0);
            return true;
        }
        else if(id == R.id.timeframeEditAlarmActivity_monday)
        {
            toggleDayButton(1);
            return true;
        }
        else if(id == R.id.timeframeEditAlarmActivity_tuesday)
        {
            toggleDayButton(2);
            return true;
        }
        else if(id == R.id.timeframeEditAlarmActivity_wednesday)
        {
            toggleDayButton(3);
            return true;
        }
        else if(id == R.id.timeframeEditAlarmActivity_thursday)
        {
            toggleDayButton(4);
            return true;
        }
        else if(id == R.id.timeframeEditAlarmActivity_friday)
        {
            toggleDayButton(5);
            return true;
        }
        else if(id == R.id.timeframeEditAlarmActivity_saturday)
        {
            toggleDayButton(6);
            return true;
        }
        return false;
    }

    //called from dayClicked(View view)
    private void toggleDayButton(int i)
    {
        if(days[i])
        {
            dayButtons[i].setBackgroundResource(R.drawable.rounded_button_inv);
            days[i] = false;
            dayButtons[i].setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }
        else
        {
            dayButtons[i].setBackgroundResource(R.drawable.rounded_button);
            days[i] = true;
            dayButtons[i].setTextColor(textColorWhenUnClicked);
        }
    }

    //display time edit dialog
    public void showDialog(int hour, int minute)
    {
        //set up dialog
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.two_dialog_time_picker);
        dialog.setTitle("SET TIME"); //this is not shown in application 6.0 or higher, shown in 4.4.4
        dialog.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!

        timePicker = (TimePicker)dialog.findViewById(R.id.dialogTimePicker_timePicker);
        //TODO getHour() works for marsh-mellow but not older versions
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);

        //set up button
        TextView cancel = (TextView) dialog.findViewById(R.id.dialogTimePicker_cancelButton);
        cancel.setOnClickListener(this);

        TextView okay = (TextView) dialog.findViewById(R.id.dialogTimePicker_OKButton);
        okay.setOnClickListener(this);

        //now that the dialog is set up, it's time to show it
        dialog.show();
    }

    //getter methods
    public int getHourStart()
    {
        return hourStart;
    }

    public int getMinuteStart()
    {
        return minuteStart;
    }

    public boolean[] getDays()
    {
        return days;
    }
}
