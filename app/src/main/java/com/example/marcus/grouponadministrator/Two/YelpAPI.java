package com.example.marcus.grouponadministrator.Two;

import android.location.Location;

import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;

/**
 * Created by Marcus Chiu on 4/21/2016.
 */
public class YelpAPI
{
    public static com.yelp.clientlib.connection.YelpAPI yelpAPI;
    private static boolean isYelpAPICreated = false;

    public static void yelpFactory()
    {
        //Create a new YelpAPI object through YelpAPIFactory with the codes as parameters
        YelpAPIFactory apiFactory = new YelpAPIFactory(
                "f4GC0YHeZW-bOh6hDV-4JA",               //Consumer Key
                "o5YGHeHFOUUjCDw_davOA7UevhI",          //Consumer Secret
                "lzD_uwdQwDFoaKS02xkZuvIWoeuyC7vD",     //Token
                "GdaBB3unzKFr7angeE6YpCr1z0U");         //Token Secret
        yelpAPI = apiFactory.createAPI();

        isYelpAPICreated = true;
    }

    public static void callYelp(ArrayList<String> terms, Location location, Callback<SearchResponse> callback)
    {
        if(!isYelpAPICreated) yelpFactory();

        //Search parameters are put into a HashMap object
        Map<String, String> params = new HashMap<>();

        //https://www.yelp.com/developers/documentation/v2/search_api for all parameters
        if(terms == null || terms.size() == 0)
            params.put("term", "");
        else
        {
            String term = terms.get(0);
            if(term != null)
                params.put("term", terms.get(0));
            else
                params.put("term", "");
        }


        params.put("limit", "20");

        // Location can be set a GPS coordinates as a CoordinateOptions object
        CoordinateOptions coordinate = CoordinateOptions.builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();

        //Create Call object passing location and parameters
        Call<SearchResponse> call = yelpAPI.search(coordinate, params);
        call.enqueue(callback);
    }

    private static boolean doesBusinessExistInList(Business business, ArrayList<Business> businessList) throws Exception
    {
        if(business == null) throw new Exception("business is null");
        if(businessList == null) throw new Exception("businessList is null");

        String id = business.id();
        for(Business b : businessList)
        {
            if(b.id().equals(id))
                return true;
        }
        return false;
    }

    private static ArrayList<Business> getNewBusinesses(ArrayList<Business> newBusinessList, ArrayList<Business> oldBusinessList)
    {
        ArrayList<Business> newBusinesses = new ArrayList<>();

        if(newBusinessList == null || newBusinessList.size() == 0) return newBusinesses;
        if(oldBusinessList == null || oldBusinessList.size() == 0) return newBusinessList;

        try
        {
            for(Business b : newBusinessList)
                if(!doesBusinessExistInList(b, oldBusinessList))
                    newBusinesses.add(b);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return newBusinesses;
    }

    public static ArrayList<Business> getUpdatedBusinessList(ArrayList<Business> newBusinessList, ArrayList<Business> oldBusinessList, int maxBusinesses2Return)
    {
        if(newBusinessList == null || newBusinessList.size() == 0)
        {
            if(oldBusinessList == null)
                return new ArrayList<>();
            else if(oldBusinessList.size() > maxBusinesses2Return)
            {
                for (int i = oldBusinessList.size(); i > maxBusinesses2Return; i--)
                    oldBusinessList.remove(i-1);
            }
            else
                return oldBusinessList;
        }

        ArrayList<Business> newBusinesses = getNewBusinesses(newBusinessList, oldBusinessList);

        //if new businesses size is greater or equal to maxBusinessSize
        //concatenate then return
        if(newBusinesses.size() >= maxBusinesses2Return)
        {
            for (int i = newBusinesses.size(); i > maxBusinesses2Return; i--)
                newBusinesses.remove(i-1);

            return newBusinesses;
        }
        //else try to append some more businesses from old business list
        //but if oldBusinessList is null, return just
        else if(oldBusinessList != null)
        {
            int numMoreBussinesses = maxBusinesses2Return - newBusinesses.size();
            if(numMoreBussinesses < oldBusinessList.size())
            {
                for(int i = 0; i < numMoreBussinesses; i++)
                    newBusinesses.add(oldBusinessList.get(i));
            }
            else
            {
                numMoreBussinesses = oldBusinessList.size();

                for(int i = 0; i < numMoreBussinesses; i++)
                    newBusinesses.add(oldBusinessList.get(i));
            }
        }
        return newBusinesses;
    }
}
