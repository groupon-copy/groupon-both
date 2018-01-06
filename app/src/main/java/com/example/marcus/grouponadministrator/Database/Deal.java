package com.example.marcus.grouponadministrator.Database;

import com.example.marcus.grouponadministrator.Config.DatabaseVariable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Marcus on 6/29/2016.
 */
public class Deal implements Serializable
{
    //database variables
    private int id;
    private int vendor_id;
    private String bold_text;
    private String fine_print_text;
    private String highlight_text;
    private String img_rel_url;
    private String valid_from;
    private String valid_until;
    private double original_price;
    private double current_price;
    private int num_bought;
    private int num_thumbs_up;
    private int num_thumbs_down;
    private boolean is_limited_time_offer;
    private boolean is_limited_availability;

    //other variables
    private String JSON_string;
    private int discount;
    //private Drawable image; images can't be serializable

    public Deal()
    {
    }

    public Deal(JSONObject jo) throws JSONException
    {
        //set database variables
        id = jo.getInt(DatabaseVariable.DEAL_ID);
        vendor_id = jo.getInt(DatabaseVariable.DEAL_VENDOR_ID);
        bold_text = jo.getString(DatabaseVariable.DEAL_BOLD_TEXT);
        fine_print_text = jo.getString(DatabaseVariable.DEAL_FINE_PRINT_TEXT);
        img_rel_url = jo.getString(DatabaseVariable.DEAL_IMG_REL_URL);
        highlight_text = jo.getString(DatabaseVariable.DEAL_HIGHLIGHT_TEXT);
        valid_from = jo.getString(DatabaseVariable.DEAL_VALID_FROM);
        valid_until = jo.getString(DatabaseVariable.DEAL_VALID_UNTIL);
        original_price = jo.getDouble(DatabaseVariable.DEAL_ORIGINAL_PRICE);
        current_price = jo.getDouble(DatabaseVariable.DEAL_CURRENT_PRICE);
        num_bought = jo.getInt(DatabaseVariable.DEAL_NUM_BOUGHT);
        num_thumbs_up = jo.getInt(DatabaseVariable.DEAL_NUM_THUMBS_UP);
        num_thumbs_down = jo.getInt(DatabaseVariable.DEAL_NUM_THUMBS_DOWN);
        if(jo.getInt(DatabaseVariable.DEAL_IS_LIMITED_TIME_OFFER) == 1) is_limited_time_offer = true; else is_limited_time_offer = false;
        if(jo.getInt(DatabaseVariable.DEAL_IS_LIMITED_AVAILABILITY) == 1) is_limited_availability = true; else is_limited_availability = false;

        //set other variables
        JSON_string = jo.toString();
        discount = (int)(100 * (1.0 - (current_price/original_price)));
    }

    public String toString()
    {
        return JSON_string;
    }

    //getters, no setters
    public int getID() {
        return id;
    }

    public int getVendorID() {
        return vendor_id;
    }

    public String getBoldText() {
        return bold_text;
    }

    public String getFinePrintText() {
        return fine_print_text;
    }

    public String getImageURL() {
        return DatabaseVariable.DEAL_IMAGES_URL + img_rel_url;
    }

    public String getImageRelativeURL() { return img_rel_url; }

    public String getValidFrom() {
        return valid_from;
    }

    public String getValidUntil() {
        return valid_until;
    }

    public String getHighlightText() { return highlight_text; }

    public double getOriginalPrice() { return original_price; }

    public double getCurrentPrice() { return current_price; }

    public int getNumBought() { return num_bought; }

    public int getNumThumbsUp() { return num_thumbs_up; }

    public int getNumThumbsDown() { return num_thumbs_down; }

    public boolean getIsLimitedTimeOffer() {return is_limited_time_offer; }

    public boolean getIsLimitedAvailability() { return is_limited_availability; }

    public String getOriginalPriceString() { return Double.toString(original_price); }

    public String getCurrentPriceString() { return Double.toString(current_price); }

    public int getDiscount() { return discount; }
}
