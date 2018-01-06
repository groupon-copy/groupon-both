package com.example.marcus.grouponadministrator._NotUsed;

/**
 * Created by Bubba on 3/25/2016.
 */
/*
public class PostProcessor
{
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
}*/
