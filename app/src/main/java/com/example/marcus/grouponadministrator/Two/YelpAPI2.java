package com.example.marcus.grouponadministrator.Two;

import android.location.Location;

import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Marcus Chiu on 4/26/2016.
 */
public class YelpAPI2
{
    private static ReentrantLock reentrantLock1 = new ReentrantLock(true);
    private static ReentrantLock reentrantLock2 = new ReentrantLock(true);
    private static int num_terms;
    private static ArrayList<Business> businesses;

    public static com.yelp.clientlib.connection.YelpAPI yelpAPI;
    private static boolean isYelpAPICreated = false;

    private static int maxReturnSize;
    private static int limit;
    private static OnYelpAPI mListener;

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

    //
    public static void callYelp(OnYelpAPI onYelpAPI, ArrayList<String> terms, Location location, int returnSize, ArrayList<Business> oldBusinesses)
    {
        if(!isYelpAPICreated) yelpFactory();

        //if callYelp again and we haven't finished
        reentrantLock1.lock();

        businesses = oldBusinesses;

        maxReturnSize = returnSize;

        if(terms.size() <= 0)
            limit = returnSize;
        else
            limit = returnSize/terms.size();

        if(limit < 1)
            limit = 1;

        //set mListener Interface
        if (onYelpAPI == null)
            return;
        else
            mListener = onYelpAPI;

        //Search parameters are put into a HashMap object
        Map<String, String> params = new HashMap<>();

        //https://www.yelp.com/developers/documentation/v2/search_api for all parameters
        if(terms == null || terms.size() == 0)
            params.put("term", "");
        else
        {
            setupYelpSearch(terms, location.getLatitude(), location.getLongitude());
        }
    }

    private static void setupYelpSearch(ArrayList<String> terms, double latitude, double longitude)
    {
        //reset businesses list
        businesses = new ArrayList<>();

        //use this to determine when to send broadcast
        num_terms = terms.size();

        //make separate call for each term
        businesses = new ArrayList<>();
        for(String x : terms)
        {
            doYelpSearch(x, latitude, longitude);
        }
    }

    public static void doYelpSearch(String search, double latitude, double longitude)
    {
        //do yelp stuff
        /*YelpAPIFactory apiFactory = new YelpAPIFactory(
                "f4GC0YHeZW-bOh6hDV-4JA",               //Consumer Key
                "o5YGHeHFOUUjCDw_davOA7UevhI",          //Consumer Secret
                "lzD_uwdQwDFoaKS02xkZuvIWoeuyC7vD",     //Token
                "GdaBB3unzKFr7angeE6YpCr1z0U");         //Token Secret
        com.yelp.clientlib.connection.YelpAPI yelpAPI = apiFactory.createAPI();*/

        Map<String, String> params = new HashMap<>();

        params.put("term", search);
        params.put("sort", "1");
        params.put("limit", Integer.toString(limit));

        CoordinateOptions coordinate = CoordinateOptions.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();

        Call<SearchResponse> call = yelpAPI.search(coordinate, params);

        Callback<SearchResponse> callback = new Callback<SearchResponse>()
        {
            @Override
            public void onResponse(Response<SearchResponse> response, Retrofit retrofit)
            {
                //call dibs to preserve integrity of the list
                reentrantLock2.lock();

                //businesses.addAll(response.body().businesses());
                ArrayList<Business> newBusinesses = response.body().businesses();
                businesses = YelpAPI.getUpdatedBusinessList(newBusinesses, businesses, maxReturnSize);

                //have we run through all the search terms?
                num_terms--;
                if (num_terms == 0)
                    sendBackToActivity();

                //relinquish access to the list
                reentrantLock2.lock();
            }

            @Override
            public void onFailure(Throwable t)
            {
                // HTTP error happened, do something to handle it.

                //call dibs to preserve integrity of the list
                reentrantLock2.lock();

                //have we run through all the search terms?
                num_terms--;
                if(num_terms == 0)
                    sendBackToActivity();

                //relinquish access to the list
                reentrantLock2.lock();
            }
        };

        call.enqueue(callback);
    }

    private static void sendBackToActivity()
    {
        mListener.OnYelpAPIResults(businesses);

        //allow other threads to access setupYelpSearch
        reentrantLock1.unlock();
    }

    public interface OnYelpAPI
    {
        void OnYelpAPIResults(ArrayList<Business> businesses);
    }
}
