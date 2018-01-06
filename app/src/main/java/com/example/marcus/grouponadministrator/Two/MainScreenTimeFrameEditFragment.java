package com.example.marcus.grouponadministrator.Two;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.marcus.grouponadministrator.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMainScreenTimeFrameEditFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainScreenTimeFrameEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainScreenTimeFrameEditFragment extends Fragment implements View.OnClickListener
{
    //parameter arguments, choose names that match
    private static final String ARG_INDEX = "timeframeEditIndex";
    private static final String ARG_NAME = "timeframeEditName";
    private static final String ARG_ONOFF = "timeframeEditONOFF";
    private static final String ARG_START_END_TIME = "timeframeEditStartEndTime";
    private static final String ARG_TAG_LIST = "timeframeEditTagList";

    //types of parameters
    private int index;
    private String name;
    private String tagListString;
    private boolean ONorOFF;
    private String startTime;

    Switch onOffSwitch;

    private OnMainScreenTimeFrameEditFragmentInteractionListener mListener;

    public MainScreenTimeFrameEditFragment()
    {
        // Required empty public constructor
    }

    public static MainScreenTimeFrameEditFragment newInstance(int index, String name, boolean ONorOFF, String startTime, String tagListString)
    {
        MainScreenTimeFrameEditFragment fragment = new MainScreenTimeFrameEditFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        args.putString(ARG_NAME, name);
        args.putBoolean(ARG_ONOFF, ONorOFF);
        args.putString(ARG_START_END_TIME, startTime);
        args.putString(ARG_TAG_LIST, tagListString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            index = getArguments().getInt(ARG_INDEX);
            name = getArguments().getString(ARG_NAME);
            ONorOFF = getArguments().getBoolean(ARG_ONOFF);
            startTime = getArguments().getString(ARG_START_END_TIME);
            tagListString = getArguments().getString(ARG_TAG_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.two_fragment_main_screen_time_frame_edit, container, false);

        TextView upperText = (TextView)view.findViewById(R.id.mainScreenTimeframeEditFragment_upperText);
        TextView lowerText = (TextView)view.findViewById(R.id.mainScreenTimeframeEditFragment_lowerText);
        TextView tagListTextView = (TextView)view.findViewById(R.id.mainScreenTimeframeEditFragment_tagListTextView);
        onOffSwitch = (Switch)view.findViewById(R.id.mainScreenTimeframeEditFragment_onOffSwitch);
        onOffSwitch.setOnClickListener(this);

        tagListTextView.setText(tagListString);
        upperText.setText(startTime);
        lowerText.setText(name);
        onOffSwitch.setChecked(ONorOFF);

        //set OnClickListener
        TextView leftTextView = (TextView) view.findViewById(R.id.mainScreenTimeframeEditFragment_moveTextView);
        leftTextView.setOnClickListener(this);

        //set middle layout of fragment OnClickListener
        LinearLayout middleBodyLL = (LinearLayout)view.findViewById(R.id.mainScreenTimeframeEditFragment_middleBodyLL);
        middleBodyLL.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnMainScreenTimeFrameEditFragmentInteractionListener)
        {
            mListener = (OnMainScreenTimeFrameEditFragmentInteractionListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.mainScreenTimeframeEditFragment_middleBodyLL)
        {
            mListener.onMainScreenTimeFrameEditFragmentInteractionClicked(index);
        }
        else if(v.getId() == R.id.mainScreenTimeframeEditFragment_moveTextView)
        {
            mListener.onMainScreenTimeFrameEditFragmentInteractionDelete(index);
        }
        else if(v.getId() == R.id.mainScreenTimeframeEditFragment_onOffSwitch)
        {
            mListener.onMainScreenTimeFrameEditFragmentInteractionONOFF(index, onOffSwitch.isChecked());
        }
    }

    public interface OnMainScreenTimeFrameEditFragmentInteractionListener
    {
        void onMainScreenTimeFrameEditFragmentInteractionDelete(int index);

        void onMainScreenTimeFrameEditFragmentInteractionClicked(int index);

        void onMainScreenTimeFrameEditFragmentInteractionONOFF(int index, boolean state);
    }

    public void setIndex(int index)
    {
        this.index = index;
    }
}
