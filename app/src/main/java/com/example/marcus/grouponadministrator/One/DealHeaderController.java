package com.example.marcus.grouponadministrator.One;

import android.view.View;
import android.widget.TextView;

import com.example.marcus.grouponadministrator.R;

/**
 * Created by Marcus on 6/20/2016.
 */
public class DealHeaderController
{
    private View dealHeader;

    //if -1 or false, do not display it
    private String mBoldText = "";
    private String mLocation = "";
    private double mDistance = -1.0;
    private int mDiscount = -1;
    private int mBought = -1;
    private boolean mIsLimitedAvailability = true;
    private double mOriginalPrice = -1.0;
    private double mCurrentPrice = -1.0;
    private boolean mDisplayBottomBody = true;

    public DealHeaderController(View dealHeader, String boldText, String location, double distance, int discount, int bought, boolean isLimited, double originalPrice, double currentPrice, boolean displayBottomBody)
    {
        this.dealHeader = dealHeader;
        mBoldText = boldText;
        mLocation = location;
        mDistance = distance;
        mDiscount = discount;
        mBought = bought;
        mIsLimitedAvailability = isLimited;
        mOriginalPrice = originalPrice;
        mCurrentPrice = currentPrice;
        mDisplayBottomBody = displayBottomBody;

        fillDealHeaderViews();

        dealHeader.setVisibility(View.VISIBLE);
    }

    public void setDealHeaderVisibility(int mode)
    {
        dealHeader.setVisibility(mode);
    }

    public void setBottomBodyVisibility(int mode)
    {
        View bb = dealHeader.findViewById(R.id.dealHeader_bottomBody);
        bb.setVisibility(mode);
    }

    private void fillDealHeaderViews()
    {
        //Bold Text
        TextView boldTextView = (TextView) dealHeader.findViewById(R.id.dealHeader_boldText);
        if(mBoldText != null && mBoldText != "")
            boldTextView.setText(mBoldText);
        else
            boldTextView.setText("No Bold Text Available");

        //Location Text
        TextView locationTextView = (TextView) dealHeader.findViewById(R.id.dealHeader_generalLocationName);
        if(mLocation != null && mLocation != "")
            locationTextView.setText(mLocation);
        else
            locationTextView.setText("Not Available");

        //Distance Text
        TextView distanceTextView = (TextView) dealHeader.findViewById(R.id.dealHeader_distance);
        if(mDistance != -1.0)
            distanceTextView.setText(Double.toString(mDistance) + "mi");
        else
            distanceTextView.setText("?mi");

        if(mDisplayBottomBody)
        {
            fillBottomBodyViews(dealHeader);
            setBottomBodyVisibility(View.VISIBLE);
        }
        else
        {
            setBottomBodyVisibility(View.GONE);
        }
    }

    private void fillBottomBodyViews(View view)
    {
        //Discount Text
        TextView discountTextView = (TextView) view.findViewById(R.id.dealHeader_discount);
        if(mDiscount != -1.0)
            discountTextView.setText("Discount " + Integer.toString(mDiscount) + "%");
        else
            discountTextView.setText("Discount ?%");

        //Bought Text
        TextView boughtTextView = (TextView) view.findViewById(R.id.dealHeader_bought);
        if(mBought != -1)
            boughtTextView.setText("Bought " + Integer.toString(mBought) + "+");
        else
            boughtTextView.setText("Bought ? times");

        //Is Limited
        TextView isLimitedAvailabilityTextView = (TextView) view.findViewById(R.id.dealHeader_limitedAvailability);
        if(mIsLimitedAvailability)
            isLimitedAvailabilityTextView.setVisibility(View.VISIBLE);
        else
            isLimitedAvailabilityTextView.setVisibility(View.GONE);

        //Original Price
        TextView originalPriceTextView = (TextView) view.findViewById(R.id.dealHeader_originalPrice);
        if(mOriginalPrice != -1.0)
            originalPriceTextView.setText("$" + Double.toString(mOriginalPrice));
        else
            originalPriceTextView.setText("$?");

        //Current Price
        TextView currentPriceTextView = (TextView) view.findViewById(R.id.dealHeader_currentPrice);
        if(mCurrentPrice != -1.0)
            currentPriceTextView.setText("$" + Double.toString(mCurrentPrice));
        else
            currentPriceTextView.setText("$?");
    }
}
