package com.example.marcus.grouponadministrator.Database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marcus on 6/29/2016.
 */
public class PostProcessor
{
    //this method loads database stuff synchronously
    public static String load(URL url, HashMap<String, String> params) {
        try
        {
            //execute database query
            String string = PostProcessor.postIt(url, params);
            return string;
        }
        catch (Exception e)
        {
            e.printStackTrace();

            //either return empty string
            return "";
        }
    }

    public static String postIt(URL u, HashMap<String, String> params)
    {
        try
        {
            HttpURLConnection con = (HttpURLConnection)u.openConnection();
            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

            if (params != null)
            {
                StringBuilder outpar = new StringBuilder();
                boolean first = true;
                for (Map.Entry<String, String> e: params.entrySet())
                {
                    if (first)
                        first = false;
                    else
                        outpar.append("&");

                    outpar.append(URLEncoder.encode(e.getKey(), "UTF-8"));
                    outpar.append("=");
                    outpar.append(URLEncoder.encode(e.getValue(), "UTF-8"));
                }

                OutputStream os = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(outpar.toString());
                writer.flush();
                writer.close();
                os.close();
            }

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
