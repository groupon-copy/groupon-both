package com.example.marcus.grouponadministrator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

public class MapFragment extends Fragment implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<String>
{
    public static final String ARG_ADDRESS = "address";
    public static final String ARG_STOP_TOUCH = "stopTouch";

    //for converting address to lat and long
    private static final int MAP_LOADER_ID = 1;//must be unique in activity

    MapView mapView;
    GoogleMap mMap;
    String address;
    boolean stopTouchEvents;

    private OnMapFragmentInteractionListener mListener;

    public MapFragment()
    {
        // Required empty public constructor
    }

    public static MapFragment newInstance(String address, boolean stopTouchEvents)
    {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ADDRESS, address);
        args.putBoolean(ARG_STOP_TOUCH, stopTouchEvents);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            this.address = getArguments().getString(ARG_ADDRESS);
            this.stopTouchEvents = getArguments().getBoolean(ARG_STOP_TOUCH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        //get notified when map is ready to use
        mapView.getMapAsync(this);

        return v;
    }

    @Override
    public void onResume()
    {
        mapView.onResume();
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if (context instanceof OnMapFragmentInteractionListener)
        {
            mListener = (OnMapFragmentInteractionListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement OnMapFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        //set on click listener
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng arg0)
            {
                mListener.onMapFragmentClicked();
            }
        });

        //stop map touch events if indicated
        if(stopTouchEvents) stopTouchEvents();

        /*
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try
        {
            MapsInitializer.initialize(this.getActivity());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
        map.animateCamera(cameraUpdate);
        */

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //LatLng location = getLocationInfo(address);
        //getLocationInfo(address);
        getLoaderManager().restartLoader(MAP_LOADER_ID, null, this);
    }

    private void stopTouchEvents()
    {
        //disable touch event for mMap
        mMap.getUiSettings().setAllGesturesEnabled(false);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args)
    {
        URL url;
        try
        {
            final String google_url = "https://maps.googleapis.com/maps/api/geocode/json?address=";
            String replaceText = address;
            //replace spaces ' ' with '+'
            replaceText = replaceText.replace(' ', '+');
            //Log.e("resulting url", google_url+replaceText);

            //"https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA"
            url = new URL(google_url + replaceText);
            return new UrlLoader(getActivity(), url);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data)
    {
        //////////////////////
        //SAMPLE RETURN DATA//
        //////////////////////
        /*
        {
            "results" : [
                {
                   "address_components" : [
                      {
                         "long_name" : "1600",
                         "short_name" : "1600",
                         "types" : [ "street_number" ]
                      },
                      {
                         "long_name" : "Amphitheatre Parkway",
                         "short_name" : "Amphitheatre Pkwy",
                         "types" : [ "route" ]
                      },
                      {
                         "long_name" : "Mountain View",
                         "short_name" : "Mountain View",
                         "types" : [ "locality", "political" ]
                      },
                      {
                         "long_name" : "Santa Clara County",
                         "short_name" : "Santa Clara County",
                         "types" : [ "administrative_area_level_2", "political" ]
                      },
                      {
                         "long_name" : "California",
                         "short_name" : "CA",
                         "types" : [ "administrative_area_level_1", "political" ]
                      },
                      {
                         "long_name" : "United States",
                         "short_name" : "US",
                         "types" : [ "country", "political" ]
                      },
                      {
                         "long_name" : "94043",
                         "short_name" : "94043",
                         "types" : [ "postal_code" ]
                      }
                   ],
                   "formatted_address" : "1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA",
                   "geometry" : {
                      "location" : {
                         "lat" : 37.422364,
                         "lng" : -122.084364
                      },
                      "location_type" : "ROOFTOP",
                      "viewport" : {
                         "northeast" : {
                            "lat" : 37.4237129802915,
                            "lng" : -122.0830150197085
                         },
                         "southwest" : {
                            "lat" : 37.42101501970851,
                            "lng" : -122.0857129802915
                         }
                      }
                   },
                   "place_id" : "ChIJ2eUgeAK6j4ARbn5u_wAGqWA",
                   "types" : [ "street_address" ]
                }
             ],
             "status" : "OK"
          }
         */

        //NOW EXTRACT THE LAT AND LNG

        //do something with string data
        double lat, lng;

        try
        {
            JSONObject json0 = new JSONObject(data);
            JSONArray json1 =  json0.getJSONArray("results");
            //Log.e("json1", json1.toString());
            JSONObject json2 =  json1.getJSONObject(0);
            //Log.e("json2", json2.toString());
            JSONObject json3 = json2.getJSONObject("geometry");
            //Log.e("geometry", json3.toString());
            JSONObject json4 = json3.getJSONObject("location");
            lat = json4.getDouble("lat");
            lng = json4.getDouble("lng");

            LatLng location = new LatLng(lat, lng);

            mMap.addMarker(new MarkerOptions().position(location).title("Marker of Vendor Location"));

            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(location, 16);
            mMap.animateCamera(yourLocation);

            //OTHER OPTIONS//
            //ZOOM UP MULTIPLE MARKER OPTIONS
            /*List<MarkerOptions> markerOptions = new ArrayList<>();
            markerOptions.add(new MarkerOptions().position(location).title("Marker of Vendor Location"));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (MarkerOptions mOptions : markerOptions)
            {
                mMap.addMarker(new MarkerOptions().position(location).title("Marker of Vendor Location"));
                builder.include(mOptions.getPosition());
            }
            LatLngBounds bounds = builder.build();

            int padding = 100; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            //mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            //mMap.animateCamera(CameraUpdateFactory.newLatLng(location));
            mMap.animateCamera(cu);*/
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Log.e("MapFragment", "error extracting lat and lng");
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader)
    {

    }

    //GeoCoder way
    /*private LatLng getLatLongFromAddress(Activity activity, String address)
    {
        Geocoder geoCoder = new Geocoder(activity, Locale.getDefault());
        try
        {
            ///////////////////////////
            //This always return null//
            ///////////////////////////
            List<Address> addresses = geoCoder.getFromLocationName(address , 1);

            if (addresses.size() > 0)
            {
                //No GeoPoint in Google Maps API v2
                Log.e("location", "lat: " + addresses.get(0).getLatitude());
                Log.e("location", "long: " + addresses.get(0).getLongitude());

                LatLng location = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());

                return location;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }*/

    public interface OnMapFragmentInteractionListener
    {
        void onMapFragmentClicked();
    }
}
