package com.example.shubhampandey.parceldrop.util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SHUBHAM PANDEY on 3/6/2016.
 */
public class FetchPickupRates {
    public static JSONObject getJSON(int weight) {
        String OPEN_API =
                "https://hackerearth.0x10.info/api/ipickup?type=json&query=current_rates&weight=" + weight;
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
            return jsonObject.getJSONArray("current_rates").getJSONObject(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
