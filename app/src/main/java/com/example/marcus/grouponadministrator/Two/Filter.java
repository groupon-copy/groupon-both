package com.example.marcus.grouponadministrator.Two;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Marcus Chiu on 4/18/2016.
 */
public class Filter
{
    private boolean dollar1;
    private boolean dollar2;
    private boolean dollar3;
    private boolean dollar4;
    private boolean openNow;

    public Filter(Context context, String fileName)
    {
        readFilterFromFile(context, fileName);
    }

    public void setDollar1(boolean bool)
    {
        dollar1 = bool;
    }
    public void setDollar2(boolean bool)
    {
        dollar2 = bool;
    }
    public void setDollar3(boolean bool)
    {
        dollar3 = bool;
    }
    public void setDollar4(boolean bool)
    {
        dollar4 = bool;
    }
    public void setOpenNow(boolean bool)
    {
        openNow = bool;
    }

    public boolean getDollar1()
    {
        return dollar1;
    }
    public boolean getDollar2()
    {
        return dollar2;
    }
    public boolean getDollar3()
    {
        return dollar3;
    }
    public boolean getDollar4()
    {
        return dollar4;
    }
    public boolean getOpenNow()
    {
        return openNow;
    }

    //IO operations
    public void writeFilterToFile(Context context, String fileName)
    {
        //here we convert Filter Object to JSON
        JSONObject jsonFilter = new JSONObject();

        try
        {
            //set tags
            jsonFilter.put("dollar1", dollar1);
            jsonFilter.put("dollar2", dollar2);
            jsonFilter.put("dollar3", dollar3);
            jsonFilter.put("dollar4", dollar4);
            jsonFilter.put("openNow", openNow);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Toast.makeText(context, "Error saving Filter", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "saving blank Filter", Toast.LENGTH_SHORT).show();
            jsonFilter = new JSONObject();
        }

        FileIO.writeToFile(context, fileName, jsonFilter.toString());
    }
    public void readFilterFromFile(Context context, String fileName)
    {
        String jsonFilter = FileIO.readFromFile(context, fileName);

        if(jsonFilter == "")
        {
            dollar1 = false;
            dollar2 = false;
            dollar3 = false;
            dollar4 = false;
            openNow = false;
            return;
        }

        try
        {
            JSONObject json = new JSONObject(jsonFilter);

            dollar1 = json.getBoolean("dollar1");
            dollar2 = json.getBoolean("dollar2");
            dollar3 = json.getBoolean("dollar3");
            dollar4 = json.getBoolean("dollar4");
            openNow = json.getBoolean("openNow");
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            dollar1 = false;
            dollar2 = false;
            dollar3 = false;
            dollar4 = false;
            openNow = false;
        }
    }
}
