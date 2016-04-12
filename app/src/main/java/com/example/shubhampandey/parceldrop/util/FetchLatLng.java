package com.example.shubhampandey.parceldrop.util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SHUBHAM PANDEY on 3/5/2016.
 */

public class FetchLatLng {

    public static JSONObject getJSON(String query) {
        String source = query.replaceAll(" ", "%20");
        String OPEN_API =
                "https://maps.googleapis.com/maps/api/geocode/json?address=" + source;
        try {
            URL url = new URL(OPEN_API);
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuilder json = new StringBuilder(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();
            JSONObject jsonObject = new JSONObject(json.toString());
            return jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

