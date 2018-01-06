package com.example.marcus.grouponadministrator.Two;

import android.app.Activity;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.marcus.grouponadministrator.R;

/**
 * Created by Marcus Chiu on 4/17/2016.
 */
public class DialogFilter extends Dialog implements View.OnClickListener, LoaderManager.LoaderCallbacks<Filter>
{
    private static final String filename_filter = "filter";
    private static final int FILTER_LOADER_ID = GlobalVariables.FILTER_LOADER_ID; //must be unique

    private Activity activity;
    private Filter filter;

    private Button dollar1;
    private Button dollar2;
    private Button dollar3;
    private Button dollar4;
    private Button openNow;

    public DialogFilter(Activity activity)
    {
        super(activity);

        this.activity = activity;

        setContentView(R.layout.two_dialog_active_filter);
        setTitle("This is a Custom Filter Box");
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        //there are a lot of settings, for dialog, check them all out!
        //http://developer.android.com/reference/android/app/Dialog.html

        setTitle("FILTER"); //for previous version
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //Load Filter From File
        activity.getLoaderManager().initLoader(FILTER_LOADER_ID, null, this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        //save filter settings to file
        //pass context for file IO
        //pass file name
        filter.writeFilterToFile(activity, filename_filter);
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        switch(id)
        {
            case R.id.dialogActiveFilter_dollar1:
                if(filter.getDollar1())
                {
                    dollar1.setBackgroundResource(R.drawable.button_unselected);
                    dollar1.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
                    filter.setDollar1(false);
                }
                else
                {
                    dollar1.setBackgroundResource(R.drawable.button_selected);
                    dollar1.setTextColor(ContextCompat.getColor(activity, R.color.colorBackground));
                    filter.setDollar1(true);
                }
                break;

            case R.id.dialogActiveFilter_dollar2:
                if(filter.getDollar2())
                {
                    dollar2.setBackgroundResource(R.drawable.button_unselected);
                    dollar2.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
                    filter.setDollar2(false);
                }
                else
                {
                    dollar2.setBackgroundResource(R.drawable.button_selected);
                    dollar2.setTextColor(ContextCompat.getColor(activity, R.color.colorBackground));
                    filter.setDollar2(true);
                }
                break;

            case R.id.dialogActiveFilter_dollar3:
                if(filter.getDollar3())
                {
                    dollar3.setBackgroundResource(R.drawable.button_unselected);
                    dollar3.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
                    filter.setDollar3(false);
                }
                else
                {
                    dollar3.setBackgroundResource(R.drawable.button_selected);
                    dollar3.setTextColor(ContextCompat.getColor(activity, R.color.colorBackground));
                    filter.setDollar3(true);
                }
                break;

            case R.id.dialogActiveFilter_dollar4:
                if(filter.getDollar4())
                {
                    dollar4.setBackgroundResource(R.drawable.button_unselected);
                    dollar4.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
                    filter.setDollar4(false);
                }
                else
                {
                    dollar4.setBackgroundResource(R.drawable.button_selected);
                    dollar4.setTextColor(ContextCompat.getColor(activity, R.color.colorBackground));
                    filter.setDollar4(true);
                }
                break;

            case R.id.dialogActiveFilter_openNowButton:
                if(filter.getOpenNow())
                {
                    openNow.setBackgroundResource(R.drawable.button_unselected);
                    openNow.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
                    filter.setOpenNow(false);
                }
                else
                {
                    openNow.setBackgroundResource(R.drawable.button_selected);
                    openNow.setTextColor(ContextCompat.getColor(activity, R.color.colorBackground));
                    filter.setOpenNow(true);
                }
                break;

            case R.id.dialogActiveFilter_backTextView:
                dismiss();
                break;
        }
    }

    private void setView()
    {
        dollar1 = (Button)findViewById(R.id.dialogActiveFilter_dollar1);
        dollar1.setOnClickListener(this);
        setButtonBackGround(dollar1, filter.getDollar1());

        dollar2 = (Button)findViewById(R.id.dialogActiveFilter_dollar2);
        dollar2.setOnClickListener(this);
        setButtonBackGround(dollar2, filter.getDollar2());

        dollar3 = (Button)findViewById(R.id.dialogActiveFilter_dollar3);
        dollar3.setOnClickListener(this);
        setButtonBackGround(dollar3, filter.getDollar3());

        dollar4 = (Button)findViewById(R.id.dialogActiveFilter_dollar4);
        dollar4.setOnClickListener(this);
        setButtonBackGround(dollar4, filter.getDollar4());

        openNow = (Button)findViewById(R.id.dialogActiveFilter_openNowButton);
        openNow.setOnClickListener(this);
        setButtonBackGround(openNow, filter.getOpenNow());

        TextView cancel = (TextView) findViewById(R.id.dialogActiveFilter_backTextView);
        cancel.setOnClickListener(this);
    }

    private void setButtonBackGround(Button button, boolean bool)
    {
        if(bool)
        {
            button.setBackgroundResource(R.drawable.button_selected);
            button.setTextColor(ContextCompat.getColor(activity, R.color.colorBackground));
        }
        else
        {
            button.setBackgroundResource(R.drawable.button_unselected);
            button.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
        }
    }

    //FilterFileLoader Interface
    @Override
    public Loader<Filter> onCreateLoader(int id, Bundle args)
    {
        return new FilterFileLoader(activity);
    }
    @Override
    public void onLoadFinished(Loader<Filter> loader, Filter data)
    {
        this.filter = data;
        setView();
    }
    @Override
    public void onLoaderReset(Loader<Filter> loader)
    {

    }
}
