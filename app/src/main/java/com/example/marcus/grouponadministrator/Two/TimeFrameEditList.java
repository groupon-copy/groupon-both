package com.example.marcus.grouponadministrator.Two;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcus Chiu on 4/4/2016.
 */
public class TimeFrameEditList implements Serializable
{
    private List<TimeFrameEdit> timeFrameEditList = new ArrayList<>();

    //constructor
    public TimeFrameEditList(Context context, String fileName)
    {
        //read from file
        readTimeFrameEditListFromFile(context, fileName);
    }

    public int getSize()
    {
        return timeFrameEditList.size();
    }

    public boolean doestimeframeNameExist(String timeFrameName)
    {
        for(int i = 0; i < timeFrameEditList.size(); i++)
        {
            if(timeFrameEditList.get(i).name.equals(timeFrameName))
                return true;
        }
        return false;
    }
    public boolean doesTimeFrameIDExist(int timeFrameID)
    {
        for(TimeFrameEdit tf: timeFrameEditList)
        {
            if(tf.timeFrameID == timeFrameID)
                return true;
        }
        return false;
    }
    public int getUniqueTimeFrameEditID()
    {
        int id = 0;
        while(doesTimeFrameIDExist(id))
        {
            id++;
        }

        return id;
    }
    //returns index of new timeframe edit
    public int createDefaultTimeframeEdit()
    {
        //create new timeframeEdit
        TimeFrameEdit timeFrameEdit = new TimeFrameEdit();

        //add timeframe edit into list
        timeFrameEditList.add(timeFrameEdit);

        //set defualt name
        String newName;
        int start = 0;
        do
        {
            newName = timeFrameEdit.name + " " + start;
            start++;
        }while(doestimeframeNameExist(newName));
        timeFrameEdit.name = newName;

        //set TimeFrame Edit ID
        timeFrameEdit.timeFrameID = getUniqueTimeFrameEditID();

        //set AlarmIDs
        for(int i = 0; i < timeFrameEdit.alarmIDs.length; i++)
        {
            timeFrameEdit.alarmIDs[i] = (timeFrameEdit.timeFrameID * 10) + i;
        }

        //no need to set up alarms

        return (timeFrameEditList.size()-1);
    }
    public void updateTimeframeEdit(Context context, int index, String name, int startHour, int startMinute, boolean[] daysOn, ArrayList<String> tagList, boolean ONorOFF)
    {
        //create new TimeFrameEdit
        TimeFrameEdit timeFrameEdit = timeFrameEditList.get(index);

        //delete alarms of this timeFrameEdit
        deleteTimeFrameAlarms(context, index);

        //set the variables
        timeFrameEdit.ONorOFF = ONorOFF;
        timeFrameEdit.name = name;
        timeFrameEdit.startHour = startHour;
        timeFrameEdit.startMinute = startMinute;
        timeFrameEdit.daysOn = daysOn;
        timeFrameEdit.tagList = tagList;

        //create new set of alarms
        createTimeFrameAlarms(context, index);
    }
    public void removeTimeframeEdit(Context context, int index)
    {
        TimeFrameEdit timeFrameEdit = timeFrameEditList.get(index);

        //delete alarms of this timeFrameEdit
        deleteTimeFrameAlarms(context, index);

        timeFrameEditList.remove(index);
    }
    private void createTimeFrameAlarms(Context context, int index)
    {
        TimeFrameEdit timeFrameEdit = timeFrameEditList.get(index);

        //create new set of alarms
        ArrayList<Integer> requestCodes = new ArrayList<>();
        ArrayList<Integer> daysOfTheWeek = new ArrayList<>();
        for(int i = 0; i < 7; i++)
        {
            //if this day is ON, update requestCodes and daysOfTheWeek
            if(timeFrameEdit.daysOn[i])
            {
                requestCodes.add(timeFrameEdit.alarmIDs[i]);

                //AlarmEditor takes days ranging from 1 - 7
                //TimeFrameEditListList has days ranging from 0 - 6
                //so just add 1 to i
                daysOfTheWeek.add((i+1));
            }
        }

        int startHour = timeFrameEdit.startHour;
        int startMinute = timeFrameEdit.startMinute;

        AlarmEditor.createMultipleAlarms(context, timeFrameEdit.tagList, requestCodes, daysOfTheWeek, startHour, startMinute);
    }
    private void deleteTimeFrameAlarms(Context context, int index)
    {
        TimeFrameEdit timeFrameEdit = timeFrameEditList.get(index);

        //delete alarms based on daysOn
        int[] alarmIDs = timeFrameEdit.alarmIDs;
        ArrayList<Integer> alarmIDsToDelete = new ArrayList<>();
        //for each day, check if it is on
        for(int i = 0; i < 7; i++)
        {
            //if this day is ON, add alarmID of that day to the trash
            if(timeFrameEdit.daysOn[i])
                alarmIDsToDelete.add(alarmIDs[i]);
        }
        AlarmEditor.deleteMultipleAlarms(context, alarmIDsToDelete);
    }

    public ArrayList<String> getTagArrayListofAllOnTimeFrameEdits()
    {
        ArrayList<String> tagList = new ArrayList<>();

        for(TimeFrameEdit t: timeFrameEditList)
        {
            if(t.ONorOFF)
            {
                for(String s: t.tagList)
                    tagList.add(s);
            }
        }

        return tagList;
    }

    //FILE IO METHODS
    public void writeTimeFrameEditListToFile(Context context, String filename_timeFrameEditList)
    {
        //here we convert TimeframeEdit List Object to JSON
        JSONObject jsonTimeframeEditList = new JSONObject();

        try
        {
            //set tags
            JSONArray jsonArrayTimeframeEdits = new JSONArray();
            for(int i = 0; i < timeFrameEditList.size(); i++)
            {
                JSONObject timeframeEdit = timeFrameEdit2JsonObject(timeFrameEditList.get(i));
                if(timeframeEdit != null)
                    jsonArrayTimeframeEdits.put(timeframeEdit);
                else
                    Log.e("ERROR", "timeframeEditList2JsonObject: returned a null object");
            }
            jsonTimeframeEditList.put("timeframeEditList", jsonArrayTimeframeEdits);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Toast.makeText(context, "Error saving TimeFrameEditList", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "saving blank TimeFrameEditList", Toast.LENGTH_SHORT).show();
            jsonTimeframeEditList = new JSONObject();
        }

        FileIO.writeToFile(context, filename_timeFrameEditList, jsonTimeframeEditList.toString());
    }
    public void readTimeFrameEditListFromFile(Context context, String filename_timeFrameEditList)
    {
        String jsonTimeframeList = FileIO.readFromFile(context, filename_timeFrameEditList);

        if(jsonTimeframeList == "")
        {
            timeFrameEditList = new ArrayList<>();
            return;
        }

        List<TimeFrameEdit> timeFrameEditList = new ArrayList<>();
        try
        {
            JSONObject jsonObject = new JSONObject(jsonTimeframeList);

            JSONArray jsonArray = jsonObject.getJSONArray("timeframeEditList");
            for(int i = 0; i < jsonArray.length(); i++)
            {
                TimeFrameEdit timeFrameEdit = json2TimeFrameEdit(jsonArray.getJSONObject(i));
                timeFrameEditList.add(timeFrameEdit);
            }

            this.timeFrameEditList = timeFrameEditList;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            this.timeFrameEditList = new ArrayList<>();
        }
    }
    private JSONObject timeFrameEdit2JsonObject(TimeFrameEdit edit)
    {
        try
        {
            //here we convert TimeframeEdit Object to JSON
            JSONObject jsonTimeframeEdit = new JSONObject();

            //set Time Farme ID
            jsonTimeframeEdit.put("ID", edit.timeFrameID);

            //set ON or OFF
            jsonTimeframeEdit.put("ONorOFF", edit.ONorOFF);

            //set name
            jsonTimeframeEdit.put("name", edit.name);

            //set time stuff
            jsonTimeframeEdit.put("startHour", edit.startHour);
            jsonTimeframeEdit.put("startMinute", edit.startMinute);

            jsonTimeframeEdit.put("sunday", edit.daysOn[0]);
            jsonTimeframeEdit.put("monday", edit.daysOn[1]);
            jsonTimeframeEdit.put("tuesday", edit.daysOn[2]);
            jsonTimeframeEdit.put("wednesday", edit.daysOn[3]);
            jsonTimeframeEdit.put("thursday", edit.daysOn[4]);
            jsonTimeframeEdit.put("friday", edit.daysOn[5]);
            jsonTimeframeEdit.put("saturday", edit.daysOn[6]);

            //set search edit names
            JSONArray jsonArrayTags = new JSONArray();
            for(int i = 0; i < edit.tagList.size(); i++)
            {
                JSONObject searchEditNames = new JSONObject();
                searchEditNames.put("searchEditName", edit.tagList.get(i));
                jsonArrayTags.put(searchEditNames);
            }
            jsonTimeframeEdit.put("searchEditNames", jsonArrayTags);

            return jsonTimeframeEdit;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    private TimeFrameEdit json2TimeFrameEdit(JSONObject json)
    {
        TimeFrameEdit timeFrameEdit = new TimeFrameEdit();
        try
        {
            //set time frame edit ID
            timeFrameEdit.timeFrameID = json.getInt("ID");

            //set alarm IDs
            for(int i = 0; i < timeFrameEdit.alarmIDs.length; i++)
            {
                timeFrameEdit.alarmIDs[i] = (timeFrameEdit.timeFrameID * 10) + i;
            }

            //set ON or OFF
            timeFrameEdit.ONorOFF = json.getBoolean("ONorOFF");

            //set name
            timeFrameEdit.name = json.getString("name");

            //set time stuff
            timeFrameEdit.startHour = json.getInt("startHour");
            timeFrameEdit.startMinute = json.getInt("startMinute");

            timeFrameEdit.daysOn[0] = json.getBoolean("sunday");
            timeFrameEdit.daysOn[1] = json.getBoolean("monday");
            timeFrameEdit.daysOn[2] = json.getBoolean("tuesday");
            timeFrameEdit.daysOn[3] = json.getBoolean("wednesday");
            timeFrameEdit.daysOn[4] = json.getBoolean("thursday");
            timeFrameEdit.daysOn[5] = json.getBoolean("friday");
            timeFrameEdit.daysOn[6] = json.getBoolean("saturday");

            //set tags
            ArrayList<String> tags = new ArrayList<>();
            JSONArray jArray = json.getJSONArray("searchEditNames");
            for(int i = 0; i < jArray.length(); i++)
            {
                JSONObject obj = jArray.getJSONObject(i);
                tags.add(obj.getString("searchEditName"));
            }
            timeFrameEdit.tagList = tags;

            return timeFrameEdit;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private class TimeFrameEdit implements Serializable
    {
        //variables
        int timeFrameID;
        boolean ONorOFF;
        String name;
        int startHour;
        int startMinute;
        // S M T W T F S
        // in that order
        boolean[] daysOn;
        ArrayList<String> tagList;
        int[] alarmIDs;

        private TimeFrameEdit()
        {
            timeFrameID = -1;
            ONorOFF = true;
            name = "Schedule";
            startHour = 11;
            startMinute = 30;
            daysOn = new boolean[]{false, false, false, false, false, false, false};
            tagList = new ArrayList<>();
            alarmIDs = new int[]{-1, -1, -1, -1, -1, -1 ,-1};
        }
    }

    //ON OFF set and get
    public void setONorOFF(Context context, int index, boolean ONorOFF)
    {
        timeFrameEditList.get(index).ONorOFF = ONorOFF;

        //if set to on, create alarms, otherwise delete existing alarms of this timeframe edit
        if(ONorOFF)
            createTimeFrameAlarms(context, index);
        else
            deleteTimeFrameAlarms(context, index);
    }
    public boolean getONorOFF(int index)
    {
        return timeFrameEditList.get(index).ONorOFF;
    }

    //name set and get
    public String getName(int index)
    {
        return timeFrameEditList.get(index).name;
    }

    //time get. NOTE if you set any of the Hour/Minute you must update the alarms
    public int getStartHour(int index)
    {
        return timeFrameEditList.get(index).startHour;
    }
    public int getStartMinute(int index)
    {
        return timeFrameEditList.get(index).startMinute;
    }

    //days get. NOTE if you set any of the days you must update the alarms
    public boolean[] getDaysOn(int index)
    {
        //create copy and return it
        //reason because it passes a reference of the array
        //and we don't want them to edit it without going through the TimeFrameEditList
        boolean[] daysOnCopy = new boolean[7];
        boolean[] daysOn = timeFrameEditList.get(index).daysOn;
        for(int i = 0; i < daysOn.length; i++)
            daysOnCopy[i] = daysOn[i];

        return daysOnCopy;
    }

    //tagList get. NOTE if you set tags you must update the alarms
    public List<String> getTagList(int index)
    {
        return timeFrameEditList.get(index).tagList;
    }
    public boolean doesTagExist(int index, String tag)
    {
        for(int i = 0; i < timeFrameEditList.get(index).tagList.size(); i++)
        {
            if(timeFrameEditList.get(index).tagList.get(i).equals(tag))
                return true;
        }
        return false;
    }

    //TimeFrame ID
    public int getTimeFrameEditID(int index)
    {
        return timeFrameEditList.get(index).timeFrameID;
    }

    //Alarm IDs
    public int[] getAlarmIDs(int index)
    {
        int[] alarmIDsCopy = new int[7];
        int[] alarmIDs = timeFrameEditList.get(index).alarmIDs;
        for(int i = 0; i < 7; i++)
            alarmIDsCopy[i] = alarmIDs[i];

        return alarmIDsCopy;
    }
}
