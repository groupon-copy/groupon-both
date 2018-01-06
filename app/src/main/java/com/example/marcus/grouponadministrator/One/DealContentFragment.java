package com.example.marcus.grouponadministrator.One;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcus.grouponadministrator.MapFragment;
import com.example.marcus.grouponadministrator.R;
import com.example.marcus.grouponadministrator.Database.Deal;
import com.example.marcus.grouponadministrator.Database.Vendor;
import com.example.marcus.grouponadministrator.Utility;

public class DealContentFragment extends Fragment
{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DEAL = "deal";
    private static final String ARG_VENDOR = "vendor";

    private Deal deal;
    private Vendor vendor;

    //private OnFragmentInteractionListener mListener;

    private LinearLayout ll;

    DealHeaderController dealHeaderController;

    private MapFragment mapFragment;

    public DealContentFragment()
    {
        // Required empty public constructor
    }

    public static DealContentFragment newInstance(Deal deal, Vendor vendor)
    {
        DealContentFragment fragment = new DealContentFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DEAL, deal);
        args.putSerializable(ARG_VENDOR, vendor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            deal = (Deal)getArguments().getSerializable(ARG_DEAL);
            vendor = (Vendor)getArguments().getSerializable(ARG_VENDOR);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.one_fragment_deal_content, container, false);

        ll = (LinearLayout)view.findViewById(R.id.dealContentFragment_verticalLinearLayout);

        createDealHeader();

        createDealContentSpacer();
        createCustomerRatingPanel();

        createDealContentSpacer();
        createHighlightsPanel();

        createDealContentSpacer();
        createBuyAsGiftPanel();

        createDealContentSpacer();
        createFinePrintPanel();

        createDealContentSpacer();
        createMapPhoneWebsite();

        return view;
    }

    private void createMapPhoneWebsite()
    {
        View mapPhoneWebsite = getActivity().getLayoutInflater().inflate(R.layout.one_deal_map_phone_website, ll, false);
        ll.addView(mapPhoneWebsite);

        //create deal content fragment
        boolean stopTouchEvents = true;
        mapFragment = MapFragment.newInstance(vendor.getFullAddress(), stopTouchEvents);

        //add fragment to activity
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.dealMapPhoneWebsite_mapHolder, mapFragment)
                .commit();

        TextView addressText = (TextView) mapPhoneWebsite.findViewById(R.id.dealMapPhoneWebsite_addressText);
        addressText.setText(vendor.getFullAddress());

        TextView phoneText = (TextView) mapPhoneWebsite.findViewById(R.id.dealMapPhoneWebsite_phoneText);
        phoneText.setText(vendor.getPhone1());

        View address = mapPhoneWebsite.findViewById(R.id.dealMapPhoneWebsite_address);
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Utility.gotoAddress(getActivity(), vendor.getFullAddress());
            }
        });

        View phone = mapPhoneWebsite.findViewById(R.id.dealMapPhoneWebsite_phone);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Utility.call(getActivity(), vendor.getPhone1());
            }
        });

        View website = mapPhoneWebsite.findViewById(R.id.dealMapPhoneWebsite_website);
        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Utility.gotoWebsite(getActivity(), vendor.getVendorWebsite());
            }
        });
    }

    private void createFinePrintPanel()
    {
        View finePrintPanel = getActivity().getLayoutInflater().inflate(R.layout.one_deal_fine_print, ll, false);

        TextView finePrintTextView = (TextView)finePrintPanel.findViewById(R.id.dealFinePrint_finePrintText);
        finePrintTextView.setText(deal.getFinePrintText());

        TextView seeTheRules = (TextView)finePrintPanel.findViewById(R.id.dealFinePrint_seeRulesThatApply);
        seeTheRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "see the rules clicked", Toast.LENGTH_SHORT).show();
            }
        });

        ll.addView(finePrintPanel);
    }

    private void createBuyAsGiftPanel()
    {
        View customerRatingPanel = getActivity().getLayoutInflater().inflate(R.layout.one_deal_buy_as_gift, ll, false);
        customerRatingPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "buy as gift clicked", Toast.LENGTH_SHORT).show();
            }
        });
        ll.addView(customerRatingPanel);
    }

    private void createCustomerRatingPanel()
    {
        View customerRatingPanel = getActivity().getLayoutInflater().inflate(R.layout.one_deal_customer_rating, ll, false);

        double sum = deal.getNumThumbsUp() + deal.getNumThumbsDown();
        double percentage = deal.getNumThumbsUp() / sum;
        percentage = Math.floor(percentage * 100);

        TextView percentageText = (TextView)customerRatingPanel.findViewById(R.id.dealCustomerRating_percentage);
        percentageText.setText(Integer.toString((int)percentage));

        TextView numCustomers = (TextView)customerRatingPanel.findViewById(R.id.dealCustomerRating_numCustomers);
        numCustomers.setText(Integer.toString((int)sum));

        ll.addView(customerRatingPanel);
    }

    private void createDealContentSpacer()
    {
        View dealContentSpacer = getActivity().getLayoutInflater().inflate(R.layout.one_deal_content_spacer, ll, false);
        ll.addView(dealContentSpacer);
    }

    private void createDealHeader()
    {
        String boldText = deal.getBoldText();
        String location = vendor.getCity();
        double distance = 10.0; //TODO
        int discount = deal.getDiscount();
        int bought = deal.getNumBought();
        boolean isLimitedAvailability = deal.getIsLimitedAvailability();
        double originalPrice = deal.getOriginalPrice();
        double currentPrice = deal.getCurrentPrice();
        boolean showBottomBody = true;
        View dealHeader = getActivity().getLayoutInflater().inflate(R.layout.one_deal_header, ll, false);
        dealHeaderController = new DealHeaderController(dealHeader, boldText, location, distance, discount, bought, isLimitedAvailability, originalPrice, currentPrice, showBottomBody);
        ll.addView(dealHeader);
    }

    private void createHighlightsPanel()
    {
        View highlightView = getActivity().getLayoutInflater().inflate(R.layout.one_deal_highlights, ll, false);
        TextView highlightsText = (TextView)highlightView.findViewById(R.id.dealHighlights_hightlightsText);
        highlightsText.setText(deal.getHighlightText());
        ll.addView(highlightView);
    }

    /*
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
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

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }*/
}
