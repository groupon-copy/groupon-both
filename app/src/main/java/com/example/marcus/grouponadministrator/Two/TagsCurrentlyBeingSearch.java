package com.example.marcus.grouponadministrator.Two;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcus Chiu on 4/24/2016.
 */
public class TagsCurrentlyBeingSearch
{
    private static final boolean DEBUG = false;
    private static final String TAG = "TagsBeingSearch";

    private static final String filename_tagsBeingSearched = "tags_being_searched";

    //returns true if tag was added to File, otherwise false
    public static boolean addTagToFile(Context context, String tag)
    {
        if(DEBUG) Log.e(TAG, "addTagToFile");

        if(tag == null || tag.equals("")) return false;

        List<String> currentTags = getTagsFromFile(context);

        if(currentTags.contains(tag))
            return false;
        else
            currentTags.add(tag);

        //here we convert Tag List Object to JSON
        JSONObject jsonTagList = new JSONObject();

        try
        {
            //set tags
            JSONArray jsonArray = new JSONArray();
            for(int i = 0; i < currentTags.size(); i++)
            {
                JSONObject searchEditNames = new JSONObject();
                searchEditNames.put("tagName", currentTags.get(i));
                jsonArray.put(searchEditNames);
            }
            jsonTagList.put("tagList", jsonArray);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Toast.makeText(context, "Error Adding Tags to File", Toast.LENGTH_SHORT).show();
        }

        FileIO.writeToFile(context, filename_tagsBeingSearched, jsonTagList.toString());

        return true;
    }

    public static void addTagsToFile(Context context, List<String> tags)
    {
        if(DEBUG) Log.e(TAG, "addTagsToFile");

        if(tags == null || tags.size() == 0) return;

        List<String> currentTags = getTagsFromFile(context);

        for (String newTag: tags)
        {
            if(!currentTags.contains(newTag))
                currentTags.add(newTag);
        }

        //here we convert Tag List Object to JSON
        JSONObject jsonTagList = new JSONObject();

        try
        {
            //set tags
            JSONArray jsonArray = new JSONArray();
            for(int i = 0; i < currentTags.size(); i++)
            {
                JSONObject searchEditNames = new JSONObject();
                searchEditNames.put("tagName", currentTags.get(i));
                jsonArray.put(searchEditNames);
            }
            jsonTagList.put("tagList", jsonArray);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Toast.makeText(context, "Error Adding Tags to File", Toast.LENGTH_SHORT).show();
        }

        FileIO.writeToFile(context, filename_tagsBeingSearched, jsonTagList.toString());
    }

    public static ArrayList<String> getTagsFromFile(Context context)
    {
        if(DEBUG) Log.e(TAG, "getTagsFromFile");

        String jsonTagList = FileIO.readFromFile(context, filename_tagsBeingSearched);

        if(jsonTagList == "")
        {
            return new ArrayList<>();
        }

        try
        {
            JSONObject jsonObject = new JSONObject(jsonTagList);

            //set search edit names
            ArrayList<String> tagListNames = new ArrayList<>();
            JSONArray jArray = jsonObject.getJSONArray("tagList");
            for(int i = 0; i < jArray.length(); i++)
            {
                JSONObject obj = jArray.getJSONObject(i);
                tagListNames.add(obj.getString("tagName"));
            }

            return tagListNames;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void clearTagsFromFile(Context context)
    {
        if(DEBUG) Log.e(TAG, "clearTagsFromFile");

        //here we convert TimeframeEdit List Object to JSON
        JSONObject jsonTagList = new JSONObject();

        try
        {
            //set tags
            JSONArray jsonEmptyArray = new JSONArray();
            jsonTagList.put("tagList", jsonEmptyArray);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Toast.makeText(context, "Error Clearing Tags From File", Toast.LENGTH_SHORT).show();
        }

        FileIO.writeToFile(context, filename_tagsBeingSearched, jsonTagList.toString());
    }

    public static void removeTagFromFile(Context context, String tag)
    {
        if(DEBUG) Log.e(TAG, "removeTagFromFile");

        ArrayList<String> tags = getTagsFromFile(context);

        tags.remove(tag);

        //here we convert Tag List Object to JSON
        JSONObject jsonTagList = new JSONObject();

        //transform arrayList tags to JSON string
        try
        {
            //set tags
            JSONArray jsonArray = new JSONArray();
            for(int i = 0; i < tags.size(); i++)
            {
                JSONObject searchEditNames = new JSONObject();
                searchEditNames.put("tagName", tags.get(i));
                jsonArray.put(searchEditNames);
            }
            jsonTagList.put("tagList", jsonArray);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Toast.makeText(context, "Error Adding Tags to File", Toast.LENGTH_SHORT).show();
        }

        //save JSON string to file
        FileIO.writeToFile(context, filename_tagsBeingSearched, jsonTagList.toString());
    }
}
