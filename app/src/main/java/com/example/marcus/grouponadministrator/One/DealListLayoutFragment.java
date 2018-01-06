package com.example.marcus.grouponadministrator.One;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.marcus.grouponadministrator.Config.DatabaseVariable;
import com.example.marcus.grouponadministrator.Database.PostUrlLoader_v4;
import com.example.marcus.grouponadministrator.R;
import com.example.marcus.grouponadministrator.Database.Deal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DealListLayoutFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>, SwipeRefreshLayout.OnRefreshListener//LoaderManager.LoaderCallbacks<List<Deal>>
{
    private static final String ARG_TAG_LIST = "tagList";
    private String tagList = "";

    private static final int LOADER_ID = 0;//must be unique in activity/fragment

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView dealListView;
    private LinearLayout emptyDealText;

    private List<Deal> dealList;

    public DealListLayoutFragment()
    {
        // Required empty public constructor
    }

    public static DealListLayoutFragment newInstance(String tagList)
    {
        DealListLayoutFragment fragment = new DealListLayoutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TAG_LIST, tagList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            tagList = getArguments().getString(ARG_TAG_LIST);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        // fragmentNumber is the loader's unique id. Loader ids are specific to the Activity or
        // Fragment in which they reside.
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.one_fragment_deal_list_layout, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.mainFeedDealListLayoutFragment_swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        dealListView = (ListView)view.findViewById(R.id.mainFeedDealListLayoutFragment_dealListView);
        dealListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ImageView imageView = (ImageView)view.findViewById(R.id.mainFeedDealFragment_imageView);
                Drawable image = imageView.getDrawable();

                DealActivity.deal = dealList.get(position);
                DealActivity.image = image;

                Intent intent = new Intent(getContext(), DealActivity.class);
                startActivity(intent);
            }
        });

        emptyDealText = (LinearLayout)view.findViewById(R.id.mainFeedDealListLayoutFragment_emptyDealText);
        emptyDealText.setVisibility(View.GONE);

        return view;
    }

    public void refreshDealListLoader()
    {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    public void refreshDealListLoaderWithNewTagsList(String tagList)
    {
        this.tagList = tagList;
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    private void updateDealListLayout(List<Deal> dealList)
    {
        if(dealList != null && dealList.size() != 0)
        {
            dealListView.setVisibility(View.VISIBLE);
            emptyDealText.setVisibility(View.GONE);

            DealListLayoutArrayAdapter dealArrayAdapter = new DealListLayoutArrayAdapter(getActivity(), 0, dealList);
            dealListView.setAdapter(dealArrayAdapter);
        }
        else
        {
            dealListView.setVisibility(View.GONE);
            emptyDealText.setVisibility(View.VISIBLE);
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

    @Override
    public void onRefresh()
    {
        swipeRefreshLayout.setRefreshing(true);
        refreshDealListLoader();
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(DatabaseVariable._POST_TAG_LIST_STRING, tagList);
            return new PostUrlLoader_v4(getContext(), new URL(DatabaseVariable.GET_DEALS_BY_TAG_LIST_URL), params);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        dealList = new ArrayList<>();

        try {
            //convert JSON String into Deal Objects
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray =  jsonObject.getJSONArray(DatabaseVariable._POST_DEALS_RETURN_STRING);

            for (int i = 0; i < jsonArray.length(); i++) {
                dealList.add(new Deal(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "error converting data to json objects", Toast.LENGTH_SHORT).show();
        } finally {
            swipeRefreshLayout.setRefreshing(false);
            updateDealListLayout(dealList);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
