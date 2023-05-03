package com.example.bot.bot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Service {

    JSONArray jsonArray;

    public Service() {
        read();
    }

    public double getCurrency(String name) {
        JSONObject json;
        for (int i = 0; i < jsonArray.length(); i ++) {
            json = jsonArray.getJSONObject(i);

            if (json.getString("Ccy").equals(name))
                return json.getDouble("Rate");
        }

        return 1d;
    }

    private void read() {
        String address = "https://cbu.uz/oz/arkhiv-kursov-valyut/json/";

        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream()));

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            connection.disconnect();

            jsonArray = new JSONArray(builder.toString());
        } catch (IOException e) {
//            e.printStackTrace();
            System.out.println("Xarolik: " + e.getMessage());
        }
    }
}
