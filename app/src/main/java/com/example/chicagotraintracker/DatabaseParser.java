package com.example.chicagotraintracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

/*
   Parses database json located in assets folder. Can be easily adapted to load from live json request,
   but seems unnecessary given the relatively consistent context of the data.

   Data source: https://data.cityofchicago.org/api/views/8pix-ypme/rows.json?accessType=DOWNLOAD
 */
class DatabaseParser {

    private HashMap<String, Station> stationData = new HashMap<>();

    DatabaseParser(BufferedReader reader) {
        String data = doRead(reader);
        parseJSON(data);
    }

    HashMap<String, Station> getStationData() {
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
                JSONArray station = data.getJSONArray(i);

                String stationName = station.getString(11);
                String mapId = station.getString(13);
                HashMap<String, Boolean> trainLines  = new HashMap<>();

                trainLines.put("red", station.getBoolean(15));
                trainLines.put("blue", station.getBoolean(16));
                trainLines.put("green", station.getBoolean(17));
                trainLines.put("brown", station.getBoolean(18));
                trainLines.put("purple", station.getBoolean(19) || station.getBoolean(20));
                trainLines.put("yellow", station.getBoolean(21));
                trainLines.put("pink", station.getBoolean(22));
                trainLines.put("orange", station.getBoolean(23));

                JSONArray location = station.getJSONArray(24);
                String lat = location.getString(1);
                String lon = location.getString(2);

                Station st = new Station(mapId, stationName, trainLines, lat, lon);

                if (stationData.containsKey(mapId)) {
                    try {
                        Station s1 = stationData.get(mapId);
                        HashMap<String, Boolean> map = s1.getTrainLines();
                        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
                            map.put(entry.getKey(), entry.getValue() || trainLines.get(entry.getKey()));
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                } else {
                    stationData.put(mapId, st);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}