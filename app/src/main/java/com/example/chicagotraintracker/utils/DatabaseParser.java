package com.example.chicagotraintracker.utils;

import android.util.Log;

import com.example.chicagotraintracker.models.Route;
import com.example.chicagotraintracker.models.Station;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.util.HashMap;

/*
   Parses database json located in assets folder. Can be easily adapted to load from live
   json request, but seems unnecessary given the relatively consistent context of the data.

   Data source: https://data.cityofchicago.org/api/views/8pix-ypme/rows.json?accessType=DOWNLOAD
 */
public class DatabaseParser {

    private static final String TAG = "DatabaseParser";
    private HashMap<String, Station> stationData = new HashMap<>();

    public DatabaseParser(BufferedReader reader) {
        String data = doRead(reader);
        parseJSON(data);
    }

    public HashMap<String, Station> getStationData() {
        return stationData;
    }

    private String doRead(BufferedReader reader) {
        StringBuilder builder = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (Exception e ) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private void parseJSON(String s) {
        try {
            JSONObject database = new JSONObject(s);
            JSONArray data = database.getJSONArray("data");

            for (int i = 0; i < data.length(); i++) {
                JSONArray jsonArray = data.getJSONArray(i);

                String stationName = jsonArray.getString(11);
                String detailedName = jsonArray.getString(12);
                String mapId = jsonArray.getString(13);
                HashMap<String, Boolean> trainLines  = new HashMap<>();

                trainLines.put(Route.RED_LINE, jsonArray.getBoolean(15));
                trainLines.put(Route.BLUE_LINE, jsonArray.getBoolean(16));
                trainLines.put(Route.GREEN_LINE, jsonArray.getBoolean(17));
                trainLines.put(Route.BROWN_LINE, jsonArray.getBoolean(18));
                trainLines.put(Route.PURPLE_LINE,
                        jsonArray.getBoolean(19) || jsonArray.getBoolean(20));
                trainLines.put(Route.YELLOW_LINE, jsonArray.getBoolean(21));
                trainLines.put(Route.PINK_LINE, jsonArray.getBoolean(22));
                trainLines.put(Route.ORANGE_LINE, jsonArray.getBoolean(23));

                JSONArray location = jsonArray.getJSONArray(24);
                String lat = location.getString(1);
                String lon = location.getString(2);

                Station station;
                if (stationData.containsKey(mapId)) {
                    station = stationData.get(mapId);
                    if (station != null) {
                        HashMap<String, Boolean> map = station.getTrainLines();
                        for (String line : map.keySet()) {
                            if (map.containsKey(line) && trainLines.containsKey(line)) {
                                try {
                                    map.put(line, map.get(line) || trainLines.get(line));
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                    Log.d(TAG, "parseJSON: Unboxing Exception");
                                }
                            }
                        }
                    }
                } else {
                    station = new Station(mapId, stationName, detailedName, trainLines, lat, lon);
                    stationData.put(mapId, station);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}