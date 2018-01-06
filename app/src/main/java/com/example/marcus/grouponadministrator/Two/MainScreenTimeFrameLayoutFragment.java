package com.example.marcus.grouponadministrator.Two;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.marcus.grouponadministrator.R;
import com.example.marcus.grouponadministrator.Utility;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainScreenTimeFrameLayoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainScreenTimeFrameLayoutFragment extends Fragment
{
    LinearLayout timeframeLayout;

    //this is used to pass the timeFrame Edit Number to TimeFrameEditActivity when clicked
    public final static String mainScreenActivity_timeframeEditNum = "timeframeEditNum";
    public final static int mainScreenActivity_defaultTimeframeEditNum = -1;

    //list of current timeFrame edit fragments
    private static List<MainScreenTimeFrameEditFragment> mainScreenTimeframeEditFragmentList = new ArrayList<>();

    //TimeFrameEditList
    //public static TimeFrameEditList timeFrameEditList;

    public MainScreenTimeFrameLayoutFragment()
    {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MainScreenTimeFrameLayoutFragment.
     */
    public static MainScreenTimeFrameLayoutFragment newInstance(TimeFrameEditList timeFrameEditList)
    {
        MainScreenTimeFrameLayoutFragment fragment = new MainScreenTimeFrameLayoutFragment();
        Bundle args = new Bundle();
        //args.putSerializable("timeFrameEditList", timeFrameEditList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            //timeFrameEditList = (TimeFrameEditList) getArguments().getSerializable("timeFramEditList");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.two_fragment_main_screen_time_frame_layout, container, false);
        timeframeLayout = (LinearLayout)view.findViewById(R.id.mainScreenTimeframeLayoutFragment_timeframeLL);
        return view;
    }

    @Override
     public void onStart()
    {
        createTimeframeEditsLayout();
        super.onStart();
    }

    private void createTimeframeEditsLayout()
    {
        if(MainScreenActivity.timeFrameEditList == null) return;

        //clear layout and ArrayList of timeframeditFragments
        mainScreenTimeframeEditFragmentList = new ArrayList<>();

        //remove all child views
        timeframeLayout.removeAllViews();

        for(int i = 0; i < MainScreenActivity.timeFrameEditList.getSize(); i++)
        {
            int index = i;
            boolean ONOFF = MainScreenActivity.timeFrameEditList.getONorOFF(i);
            String name = MainScreenActivity.timeFrameEditList.getName(i);
            String time = Utility.militaryTimeToNiceLookingTime(MainScreenActivity.timeFrameEditList.getStartHour(i), MainScreenActivity.timeFrameEditList.getStartMinute(i));
            String tagList = "";
            List<String> l = MainScreenActivity.timeFrameEditList.getTagList(index);
            for(String s: l)
            {
                tagList += s + " ";
            }

            createTimeframeEditFragment(index, ONOFF, name, time, tagList);
        }
    }

    public void addNewTimeframeEdit()
    {
        //creates timeframe edit and puts into list
        int index = MainScreenActivity.timeFrameEditList.createDefaultTimeframeEdit();

        //get variables
        boolean ONOFF = MainScreenActivity.timeFrameEditList.getONorOFF(index);
        String name = MainScreenActivity.timeFrameEditList.getName(index);
        String time = Utility.militaryTimeToNiceLookingTime(MainScreenActivity.timeFrameEditList.getStartHour(index), MainScreenActivity.timeFrameEditList.getStartMinute(index));
        String tagList = "";
        List<String> l = MainScreenActivity.timeFrameEditList.getTagList(index);
        for(String s: l)
        {
            tagList += s + " ";
        }

        createTimeframeEditFragment(index, ONOFF, name, time, tagList);

        //open TimeframeEdit Activity
        gotoTimeframeEditActivity(index);
    }

    public void createTimeframeEditFragment(int index, boolean ONOFF, String name, String time, String tagList)
    {
        //create a new Timeframe Edit Fragment
        MainScreenTimeFrameEditFragment frag = MainScreenTimeFrameEditFragment.newInstance(index, name, ONOFF, time, tagList);

        //add fragment to layout
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.mainScreenTimeframeLayoutFragment_timeframeLL, frag)
                .commitAllowingStateLoss(); //instead of commit() because of bug
        //http://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform-this-action-after-onsaveinstancestate-wit

        //add fragment to fragment list
        mainScreenTimeframeEditFragmentList.add(frag);
    }

    public void deleteTimeframeEditFragment(int index)
    {
        //remove fragment from fragment list
        mainScreenTimeframeEditFragmentList.remove(index);
        //remove fragment from layout
        timeframeLayout.removeView(timeframeLayout.getChildAt(index));

        //remove from timeframe edit list
        //pass context to remove alarms of this timeFrameEdit
        //pass index to know which timeFrame to delete
        MainScreenActivity.timeFrameEditList.removeTimeframeEdit(getContext(), index);

        refreshTimeFrameFragmentsID();
    }
    public void refreshTimeFrameFragmentsID()
    {
        for(int i = 0; i < mainScreenTimeframeEditFragmentList.size(); i++)
        {
            mainScreenTimeframeEditFragmentList.get(i).setIndex(i);
        }
    }

    public void gotoTimeframeEditActivity(int index)
    {
        Intent intent = new Intent(getContext(), TimeFrameEditActivity.class);
        intent.putExtra(mainScreenActivity_timeframeEditNum, index);
        startActivity(intent);
    }

    public void setTimeframeEditFragmentONOFF(int index, boolean state)
    {
        MainScreenActivity.timeFrameEditList.setONorOFF(getContext(), index, state);
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
}
