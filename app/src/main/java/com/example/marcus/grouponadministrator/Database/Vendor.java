package com.example.marcus.grouponadministrator.Database;

import com.example.marcus.grouponadministrator.Config.DatabaseVariable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcus on 6/29/2016.
 */
public class Vendor implements Serializable
{
    private int id;
    private String name;
    private int account_number;
    private String addr_1;
    private String addr_2;
    private String city;
    private String state;
    private String zip;
    private String country_code;
    private String phone_1;
    private String email;
    private String price_range;
    private String vendor_website;
    private int num_thumbs_up;
    private int num_thumbs_down;
    private String image_rel_url;
    private String description;
    private VendorHours hours;

    private double rating;
    private double five_star_rating;
    private boolean isOpen;

    //private Drawable image; images can't be serializable

    public Vendor(JSONObject jo) throws JSONException
    {
        id = jo.getInt(DatabaseVariable.VENDOR_ID);
        name = jo.getString(DatabaseVariable.VENDOR_NAME);
        account_number = jo.getInt(DatabaseVariable.VENDOR_ACCOUNT_NUM);
        addr_1 = jo.getString(DatabaseVariable.VENDOR_ADDR_1);
        addr_2 = jo.getString(DatabaseVariable.VENDOR_ADDR_2);
        city = jo.getString(DatabaseVariable.VENDOR_CITY);
        state = jo.getString(DatabaseVariable.VENDOR_STATE);
        zip = jo.getString(DatabaseVariable.VENDOR_ZIP);
        country_code = jo.getString(DatabaseVariable.VENDOR_COUNTRY_CODE);
        phone_1 = jo.getString(DatabaseVariable.VENDOR_PHONE_1);
        price_range = jo.getString(DatabaseVariable.VENDOR_PRICE_RANGE);
        vendor_website = jo.getString(DatabaseVariable.VENDOR_VENDOR_WEBSITE);
        num_thumbs_up = jo.getInt(DatabaseVariable.VENDOR_NUM_THUMBS_UP);
        num_thumbs_down = jo.getInt(DatabaseVariable.VENDOR_NUM_THUMBS_DOWN);
        image_rel_url = jo.getString(DatabaseVariable.VENDOR_IMAGE_REL_URL);
        description = jo.getString(DatabaseVariable.VENDOR_DESCRIPTION);
        email = jo.getString(DatabaseVariable.VENDOR_EMAIL);
        hours = new VendorHours(jo.getString(DatabaseVariable.VENDOR_HOURS));

        rating = (double) num_thumbs_up /(double) (num_thumbs_up + num_thumbs_down);
        five_star_rating = rating * 5;
        five_star_rating = Math.floor(five_star_rating * 10) / 10;
    }

    /*public Vendor(Business b)
    {
        name = b.name();

        if(b.location() != null)
        {
            if(b.location().address().size() > 0)
                addr_1 = b.location().address().get(0);

            city = b.location().city();
            state = b.location().stateCode();
            zip = b.location().postalCode();
            country = b.location().countryCode();
        }

        image_url = b.imageUrl();
        phone_1 = b.displayPhone();
        price_range = null;
        vendor_url = b.url();
        num_thumbs_up = -1;
        num_thumbs_down = -1;
        rating = b.rating().doubleValue();
        hours = null;
        description = b.snippetText();
        isOpen = !b.isClosed(); //this is not the right function. Yelp does not tell us whether it is open or not
    }*/

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddr1() {
        return addr_1;
    }

    public String getAddr2() { return addr_2; }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getCountryCode() { return country_code; }

    public String getFullAddress()
    {
        List<String> parts= new ArrayList<>();
        if(addr_1 != null && addr_1 != "")
            parts.add(addr_1);
        if(city != null && city != "")
            parts.add(city);
        if(state != null && state != "")
            parts.add(state);

        String fullAddress = "";
        if(parts.size() >= 1)
        {
            fullAddress += parts.get(0);
            for(int i = 1; i < parts.size(); i++)
            {
                fullAddress += ", " + parts.get(i);
            }
        }

        if(zip != null && zip != "")
            fullAddress += " " + zip;

        return fullAddress;
    }

    public String getPhone1() {
        return phone_1;
    }

    public String getEmail() { return email; }

    public String getPriceRange() {
        return price_range;
    }

    public int getNumThumbsUp() { return num_thumbs_up; }

    public int getNumThumbsDown() { return num_thumbs_down; }

    public String getDescription()
    {
        return description;
    }

    public VendorHours getHours()
    {
        return hours;
    }

    public String getVendorWebsite() {
        return vendor_website;
    }

    public String getImageFullURL()
    {
        return DatabaseVariable.VENDOR_IMAGES_URL + image_rel_url;
    }

    public String getImageRelativeURL() { return image_rel_url; }

    public boolean getIsOpen()
    {
        return isOpen;
    }

    public double getRating() {
        return rating;
    }

    public double getFiveStarRating() { return five_star_rating; }

    public int getIsOpenDetails()
    {
        if(hours != null)
            return VendorHours.isVendorOpenNow(hours);

            //used for business yelp API
        else if(isOpen)
            return VendorHours.OPEN_TODAY;
        else
            return VendorHours.CLOSED_TODAY;
    }
}
