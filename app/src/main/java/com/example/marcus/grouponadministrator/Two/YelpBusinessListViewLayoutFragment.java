package com.example.marcus.grouponadministrator.Two;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.marcus.grouponadministrator.R;
import com.yelp.clientlib.entities.Business;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class YelpBusinessListViewLayoutFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener
{
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView businessListView;
    private LinearLayout emptyDealText;

    private YelpBusinessListViewLayoutArrayAdapter businessArrayAdapter;
    private List<Business> businessList;

    private OnYelpBusinessListViewLayoutFragmentInteractionListener mListener;

    public YelpBusinessListViewLayoutFragment()
    {
        // Required empty public constructor
    }

    public static YelpBusinessListViewLayoutFragment newInstance()
    {
        YelpBusinessListViewLayoutFragment fragment = new YelpBusinessListViewLayoutFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
        }
        else
        {
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.two_fragment_yelp_business_list_view_layout, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.yelpBusinessListViewLayoutFragment_swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        businessListView = (ListView)view.findViewById(R.id.yelpBusinessListViewLayoutFragment_dealListView);
        businessListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                Toast.makeText(getActivity(), "replace", Toast.LENGTH_SHORT).show();
                /*ImageView imageView = (ImageView)view.findViewById(R.id.yelpBusinessFragment_image);

                VendorPageActivity.image = imageView.getDrawable();
                VendorPageActivity.vendor = new Vendor(businessList.get(position));

                Intent intent = new Intent(getContext(), VendorPageActivity.class);
                startActivity(intent);*/
            }
        });//TODO

        emptyDealText = (LinearLayout)view.findViewById(R.id.yelpBusinessListViewLayoutFragment_emptyDealText);
        emptyDealText.setVisibility(View.GONE);

        return view;
    }

    public void updateBusinessListLayout(List<Business> list)
    {
        businessList = list;

        swipeRefreshLayout.setRefreshing(false);

        if(businessList != null && businessList.size() != 0)
        {
            businessListView.setVisibility(View.VISIBLE);
            emptyDealText.setVisibility(View.GONE);

            businessArrayAdapter = new YelpBusinessListViewLayoutArrayAdapter(getActivity(), 0, businessList);
            businessListView.setAdapter(businessArrayAdapter);
        }
        else
        {
            businessListView.setVisibility(View.GONE);
            emptyDealText.setVisibility(View.VISIBLE);
        }
    }

    //goto @/strings/array_sort_order
    public void sortBusinessList(int sortType)
    {
        if(businessList != null)
        {
            switch (sortType)
            {
                case 0: //sort by shortest distance
                    Collections.sort(businessList, new Comparator<Business>(){
                        public int compare(Business b1, Business b2) {
                            return b1.distance().compareTo(b2.distance());
                        }
                    });
                    break;

                case 1: //sort by highest rating
                    Collections.sort(businessList, new Comparator<Business>(){
                        public int compare(Business b1, Business b2) {
                            return -1 * (b1.rating().compareTo(b2.rating()));
                        }
                    });
                    break;
            }

            updateBusinessListLayout(businessList);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnYelpBusinessListViewLayoutFragmentInteractionListener)
        {
            mListener = (OnYelpBusinessListViewLayoutFragmentInteractionListener) context;
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
    public void onRefresh()
    {
        swipeRefreshLayout.setRefreshing(true);
        mListener.onYelpBusinessListViewLayoutFragmentInteractionRefresh();
    }

    public interface OnYelpBusinessListViewLayoutFragmentInteractionListener
    {
        void onYelpBusinessListViewLayoutFragmentInteractionRefresh();
    }
}
