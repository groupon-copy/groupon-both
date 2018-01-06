package com.example.marcus.grouponadministrator.Two;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Marcus Chiu on 3/30/2016.
 */
public class FileIO
{
    public static boolean writeToFile(Context context, String filename, String data)
    {
        try
        {
            FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            return true;
        }
        catch (FileNotFoundException e)
        {
            Log.e("File List Service", "File not found: " + e.toString());
            Toast.makeText(context, "File to write to Not Found", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            Log.e("File List Service", "Error writing file:  " + e.toString());
            Toast.makeText(context, "Error writing file", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    public static String readFromFile(Context context, String filename)
    {
        try
        {
            String ret = "";
            FileInputStream inputStream = context.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            if ( inputStreamReader != null )
            {
                //InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null )
                {
                    stringBuilder.append(receiveString);
                }

                inputStreamReader.close();
                ret = stringBuilder.toString();
            }

            return ret;
        }
        catch (FileNotFoundException e)
        {
            //normal for the first time, it would create the first file
            Log.e("File List Service", "File not found: " + e.toString());
            //Toast.makeText(FileService.this, "File to read Not Found", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            Log.e("File List Service", "Error reading file:  " + e.toString());
            Toast.makeText(context, "Error reading file", Toast.LENGTH_SHORT).show();
        }

        return "";
    }

    public static void writeToSharedPreferences(Context context, String key, String value)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        ////SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String readFromSharedPreferences(Context context, String key)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        //SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        String defaultValue = "";
        return sharedPref.getString(key, defaultValue);
    }
}
