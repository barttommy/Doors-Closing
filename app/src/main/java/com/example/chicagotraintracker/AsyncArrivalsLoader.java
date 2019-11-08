package com.example.chicagotraintracker;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AsyncArrivalsLoader extends AsyncTask<String, Void, String> {

    private static final String TAG = "AsyncArrivalsLoader";

    private static final String API_BASE = "http://lapi.transitchicago.com/api/1.0/ttarrivals.aspx?";
    private static final String API_KEY = "73436616b5af4465bc65790aa9d4886c";

    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;

    private ArrayList<Station> requestedStations;
    private ArrayList<Route> resultList = new ArrayList<>();

    AsyncArrivalsLoader(MainActivity mainActivity, ArrayList<Station> requestedStations) {
        this.mainActivity = mainActivity;
        this.requestedStations = requestedStations;
    }

    @Override
    protected String doInBackground(String... strings) {
        for (Station station: requestedStations) {
            String stationData = getStationData(station.getMapId());
            parseJSON(stationData);
        }
        return null;
    }

    private String getStationData(String mapId) {
        StringBuilder builder = new StringBuilder();

        Uri.Builder buildURL = Uri.parse(API_BASE).buildUpon();
        buildURL.appendQueryParameter("key", API_KEY);
        buildURL.appendQueryParameter("mapid", mapId);
        buildURL.appendQueryParameter("", "40530");
        buildURL.appendQueryParameter("outputType", "JSON");
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "getStationData: " + urlToUse);

        try {
            URL url = new URL(urlToUse);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            connection.setRequestMethod("GET");
            InputStream input = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    private void parseJSON(String s) {
        try {
            JSONObject api_response = new JSONObject(s);
            JSONObject stationData = api_response.getJSONObject("ctatt");
            JSONArray trainArrivals = stationData.getJSONArray("eta");

            for (int i = 0; i < trainArrivals.length(); i++) {
                JSONObject trainData = trainArrivals.getJSONObject(i);

                String arrivalTime = trainData.getString("arrT");
                Log.d(TAG, "onPostExecute: " + arrivalTime);

                String[] timeData = parseArrivalTime(arrivalTime);

                String latitude = trainData.getString("lat");
                String longitude = trainData.getString("lon");
                String color = trainData.getString("rt");
                String stationId = trainData.getString("staId");
                String stationName = trainData.getString("staNm");
                String destination = trainData.getString("destNm");

                Train train = new Train(timeData[0], timeData[1], latitude, longitude);
                ArrayList<Train> trains = new ArrayList<>();
                trains.add(train);

                Route route = new Route(color, stationId, stationName, destination, trains);

                if (!route.getDestination().equals("See train")) {
                    int index = resultList.indexOf(route);
                    if (index != -1) {
                        if (resultList.get(index).getTrains().size() < 4) {
                            resultList.get(index).getTrains().add(train);
                        }
                    } else {
                        resultList.add(route);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "parseJSON: FAILED");
        }
    }

    @Override
    protected void onPostExecute(String s) {
        mainActivity.acceptResults(resultList);
    }

    private String currentTime() {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return formatter.format(date);
    }

    private String[] parseArrivalTime(String arrivalTime) {
        String[] result = {"", ""};
        try {
            arrivalTime = arrivalTime.substring(11);

            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Date current = format.parse(currentTime());
            Date arrival = format.parse(arrivalTime);
            long difference = (arrival.getTime() - current.getTime()) / 60000;

            Calendar c = Calendar.getInstance();
            c.setTime(arrival);
            String meridiem = (c.get(Calendar.HOUR_OF_DAY) >= 12) ? "pm" : "am";

            String minutes = String.valueOf(c.get(Calendar.MINUTE));
            minutes = (minutes.length() == 2) ? minutes : "0" + minutes;

            String timestamp = String.format("%s:%s", c.get(Calendar.HOUR), minutes);

            result[0] = String.format("Arriving at %s %s", timestamp, meridiem);
            result[1] = (difference <= 1) ? "Due" : difference + " min";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
