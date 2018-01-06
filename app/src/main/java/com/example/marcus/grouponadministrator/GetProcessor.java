package com.example.marcus.grouponadministrator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Marcus on 7/1/2016.
 */
public class GetProcessor
{
    public static String getIt(URL url)
    {
        try
        {
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);

            con.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            br.close();

            return sb.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "ERROR";
        }
    }
}
