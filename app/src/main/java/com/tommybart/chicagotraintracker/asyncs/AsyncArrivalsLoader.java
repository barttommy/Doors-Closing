package com.tommybart.chicagotraintracker.asyncs;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.tommybart.chicagotraintracker.R;
import com.tommybart.chicagotraintracker.activities.ArrivalsActivity;
import com.tommybart.chicagotraintracker.data.models.Route;
import com.tommybart.chicagotraintracker.data.models.Station;
import com.tommybart.chicagotraintracker.data.models.Train;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import static java.net.HttpURLConnection.HTTP_OK;

public class AsyncArrivalsLoader extends AsyncTask<String, Void, String> {

    private static final String TAG = "AsyncArrivalsLoader";
    private static final String API_BASE = "http://lapi.transitchicago.com/api/1.0/ttarrivals.aspx?";
    private static final int MAX_STATIONS = 4;

    @SuppressLint("StaticFieldLeak")
    private ArrivalsActivity arrivalsActivity;
    private HashSet<Station> requestedStations;
    private ArrayList<Route> resultList = new ArrayList<>();
    private boolean failed = true;
    private Instant start;

    public AsyncArrivalsLoader(ArrivalsActivity arrivalsActivity, HashSet<Station> requestedStations) {
        this.arrivalsActivity = arrivalsActivity;
        this.requestedStations = requestedStations;
    }

    @Override
    protected void onPostExecute(String s) {
        if (s == null) {
            Log.d(TAG, "onPostExecute: FAILED");
        } else if (failed){
            Log.d(TAG, "onPostExecute: FAILED: CONNECTION ERROR");
        } else {
            parseJSON(s);
            arrivalsActivity.acceptResults(resultList);
        }
        Log.d(TAG, "onPostExecute: Loaded in " +
                Duration.between(start, Instant.now()));
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
                if (resultList.contains(route)) {
                    int index = resultList.indexOf(route);
                    if (resultList.get(index).getTrains().size() < Route.ROUTE_TRAIN_LIMIT) {
                        resultList.get(index).getTrains().add(train);
                    }
                } else if (requestedStations.size() > 1) {
                    addUniqueRoutes(route);
                } else {
                    resultList.add(route);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // For location requests, only add routes if they are not already stored by a closer station
    private void addUniqueRoutes(Route route) {
        for (Route r: resultList) {
            if (r.getLine().equals(route.getLine())
                    && r.getDestination().equals(route.getDestination())) {
                return;
            }
        }
        resultList.add(route);
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
        } else if (hour == 0) {
            hour = 12;
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

    @Override
    protected String doInBackground(String... strings) {
        start = Instant.now();
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            Uri.Builder buildURL = Uri.parse(API_BASE).buildUpon();

            // See note in ArrivalsActivity.java for a public key
            buildURL.appendQueryParameter(
                    "key", arrivalsActivity.getResources().getString(R.string.cta_api_key));

            // API Call has a limit of MAX_STATIONS that can be requested
            Iterator<Station> itr = requestedStations.iterator();
            for (int i = 0; i < MAX_STATIONS && itr.hasNext(); i++) {
                buildURL.appendQueryParameter("mapid", itr.next().getMapId());
            }

            buildURL.appendQueryParameter("", "40530");
            buildURL.appendQueryParameter("outputType", "JSON");
            String urlToUse = buildURL.build().toString();
            Log.d(TAG, "downloadData: for API URL = " + urlToUse);

            URL url = new URL(urlToUse);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            String responseText = connection.getResponseMessage();

            Log.d(TAG, String.format("doInBackground: responseCode: %s responseText: %s",
                    responseCode, responseText));

            StringBuilder builder = new StringBuilder();
            if (responseCode == HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                failed = false;
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                failed = true;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            return builder.toString();
        } catch (InterruptedIOException e) {
            Log.d(TAG, "doInBackground: Thread canceled: InterruptedIOException");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}