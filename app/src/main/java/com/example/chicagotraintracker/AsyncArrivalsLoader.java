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

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.ArrayList;
import java.util.HashSet;

public class AsyncArrivalsLoader extends AsyncTask<String, Void, String> {

    private static final String TAG = "AsyncArrivalsLoader";
    private static final String API_BASE = "http://lapi.transitchicago.com/api/1.0/ttarrivals.aspx?";
    private static final String API_KEY = "73436616b5af4465bc65790aa9d4886c";

    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    private HashSet<Station> requestedStations;
    private ArrayList<Route> resultList = new ArrayList<>();

    AsyncArrivalsLoader(MainActivity mainActivity, HashSet<Station> requestedStations) {
        this.mainActivity = mainActivity;
        this.requestedStations = requestedStations;
    }

    @Override
    protected String doInBackground(String... strings) {
        for (Station station: requestedStations) {
            String api_response = downloadData(station.getMapId());
            parseJSON(api_response);
        }
        return null;
    }

    private String downloadData(String mapId) {
        StringBuilder builder = new StringBuilder();

        Uri.Builder buildURL = Uri.parse(API_BASE).buildUpon();
        buildURL.appendQueryParameter("key", API_KEY);
        buildURL.appendQueryParameter("mapid", mapId);
        buildURL.appendQueryParameter("", "40530");
        buildURL.appendQueryParameter("outputType", "JSON");
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "downloadData: for API URL = " + urlToUse);

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

                String expectedArrival = trainData.getString("arrT");
                String latitude = trainData.getString("lat");
                String longitude = trainData.getString("lon");
                String color = trainData.getString("rt");
                String stationId = trainData.getString("staId");
                String stationName = trainData.getString("staNm");
                String destination = trainData.getString("destNm");
                String formattedArrival = formatArrivalTime(expectedArrival);
                String timeRemaining = timeRemaining(expectedArrival);

                Train train = new Train(formattedArrival, timeRemaining, latitude, longitude);
                ArrayList<Train> trains = new ArrayList<>();
                trains.add(train);

                Route route = new Route(color, stationId, stationName, destination, trains);
                if (!route.getDestination().equals("See train") && resultList.contains(route)) {
                    int index = resultList.indexOf(route);
                    if (resultList.get(index).getTrains().size() < 4) {
                        resultList.get(index).getTrains().add(train);
                    }
                } else {
                    resultList.add(route);
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

    private String formatArrivalTime(String expectedArrival) {
        ZonedDateTime arrivalTime = LocalDateTime.parse(expectedArrival)
                .atZone(ZoneId.of("America/Chicago"));

        int hour = arrivalTime.getHour();
        int minutes = arrivalTime.getMinute();

        String meridiem = "am";
        if (hour > 12) {
            hour = hour - 12;
            meridiem = "pm";
        } else if (hour == 12) {
            meridiem = "pm";
        }

        String minutesString = (minutes < 10) ? "0" + minutes : Integer.toString(minutes);
        return String.format("Arriving at %s:%s %s", hour, minutesString, meridiem);
    }

    private String timeRemaining(String expectedArrival) {
        Instant arrivalTime = LocalDateTime.parse(expectedArrival)
                .atZone(ZoneId.of("America/Chicago"))
                .toInstant();
        Instant chicagoTime = ZonedDateTime.now(ZoneId.of("CST")).toInstant();
        long minutesToArrival = Duration.between(chicagoTime, arrivalTime).toMillis() / 60000;
        return (minutesToArrival <= 1) ? "Due" : minutesToArrival + " min";
    }
}