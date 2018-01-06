package com.example.marcus.grouponadministrator.One;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.marcus.grouponadministrator.R;
import com.example.marcus.grouponadministrator.Database.VendorHours;
import com.example.marcus.grouponadministrator.Utility;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VendorPageHoursIndividualFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VendorPageHoursIndividualFragment extends Fragment
{
    private static final String VENDOR_HOURS_DAY_KEY = "vendor_hours_day_key";
    private static final String VENDOR_DAY_STRING = "vendor_day_string";

    private VendorHours.VendorDay dayHours;
    private String dayString;

    public VendorPageHoursIndividualFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static VendorPageHoursIndividualFragment newInstance(VendorHours.VendorDay dayHours, String dayString)
    {
        VendorPageHoursIndividualFragment fragment = new VendorPageHoursIndividualFragment();
        Bundle args = new Bundle();
        args.putSerializable(VENDOR_HOURS_DAY_KEY, dayHours);
        args.putString(VENDOR_DAY_STRING, dayString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            this.dayHours = (VendorHours.VendorDay) getArguments().getSerializable(VENDOR_HOURS_DAY_KEY);
            this.dayString = getArguments().getString(VENDOR_DAY_STRING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.one_fragment_vendor_page_hours_individual, container, false);

        fillFragment(view);

        return view;
    }

    private void fillFragment(View view)
    {
        TextView dayTextView = (TextView)view.findViewById(R.id.vendorPageHoursIndividual_dayTextView);
        dayTextView.setText(dayString);

        if(dayHours.times.size() == 0)
        {
            TextView textView = new TextView(getActivity());
            textView.setText(dayHours.other);
            LinearLayout ll = (LinearLayout)view.findViewById(R.id.vendorPageHoursIndividual_dayLL);
            ll.addView(textView);
        }
        else
        {
            for(VendorHours.VendorStartEndTime t: dayHours.times)
            {
                String startTime = Utility.militaryTimeToNiceLookingTime(t.start.hour, t.start.minute);
                String endTime = Utility.militaryTimeToNiceLookingTime(t.end.hour, t.end.minute);

                TextView textView = new TextView(getActivity());
                textView.setText(startTime + "-" + endTime);
                LinearLayout ll = (LinearLayout)view.findViewById(R.id.vendorPageHoursIndividual_dayLL);
                ll.addView(textView);
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
}
