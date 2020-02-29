package com.example.chicagotraintracker.utils;

import android.location.Location;
import android.util.Log;

import com.example.chicagotraintracker.models.Route;
import com.example.chicagotraintracker.models.Station;
import com.example.chicagotraintracker.activities.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

// TODO Fix - addison and belmont, could be because of purple line and red line.
// TODO implementing non duplicates would probably fix this problem anyways
public class LocationHandler {

    private static final String TAG = "LocationHandler";
    private static final double LOCATION_REQUEST_RANGE_KM = 0.8;
    private static final String[] TRAIN_LINES = {
            Route.RED_LINE, Route.BLUE_LINE, Route.GREEN_LINE, Route.BROWN_LINE,
            Route.PURPLE_LINE, Route.YELLOW_LINE, Route.PINK_LINE, Route.ORANGE_LINE
    };

    private Location currentLocation;
    private ArrayList<Station> stationsInRange = new ArrayList<>();
    private HashMap<String, Boolean> linesInRange = newLinesInRange();
    private HashSet<Station> requestedStations = new HashSet<>();

    public void setLocation(Location location) {
        currentLocation = location;
        stationsInRange.clear();
        linesInRange.clear();
        requestedStations.clear();
        getNearbyStations();
        requestBestStations();
    }

   public  HashSet<Station> getRequestedStations() {
        return requestedStations;
    }

    /*
     * Searches for the best stations within range of the user. Considers all
     * available lines (as defined in linesInRange via the database) and returns the nearest
     * stations that satisfy the available lines in the area. This information can then be
     * forwarded to the AsyncArrivalsLoader to get train arrival data.
     */
    private void requestBestStations() {
        HashMap<String, Boolean> map = newLinesInRange();
        for (int i = 0; i < stationsInRange.size(); i++) {
            if (map.equals(linesInRange)) return;
            Station station = stationsInRange.get(i);
            HashMap<String, Boolean> old = new HashMap<>(map);
            updateLinesInRange(map, station.getTrainLines());
            if (!old.equals(map)) {
                requestedStations.add(station);
            }
        }
    }

    private void getNearbyStations() {
        try {
            for (Station station: MainActivity.stationData.values()) {
                double lon = Double.parseDouble(station.getLon());
                double lat = Double.parseDouble(station.getLat());
                double distance = distanceBetweenCoords(
                        currentLocation.getLatitude(), lat, currentLocation.getLongitude(), lon);
                if (distance <= LOCATION_REQUEST_RANGE_KM) {
                    station.setDistance(distance);
                    insertByDistance(station);
                    updateLinesInRange(linesInRange, station.getTrainLines());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getNearbyStations: FAILED");
        }
    }

    private void insertByDistance(Station s) {
        stationsInRange.add(s);
        double distance = s.getDistance();
        for (int i = stationsInRange.size() - 1;
             i > 0 && distance < stationsInRange.get(i-1).getDistance(); i--) {
            Collections.swap(stationsInRange, i, i-1);
        }
    }

    private HashMap<String, Boolean> newLinesInRange() {
        HashMap<String, Boolean> map = new HashMap<>();
        for (String line : TRAIN_LINES) {
            map.put(line, false);
        }
        return map;
    }

    private void updateLinesInRange(HashMap<String, Boolean> h1, HashMap<String, Boolean> h2) {
        try {
            for (String key: h1.keySet()) {
                h1.put(key, h1.get(key) || h2.get(key));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.d(TAG, "updateLinesInRange: Unboxing exception");
        }
    }

    // Haversine formula to calculate distance between coordinates
    private double distanceBetweenCoords(double lat1, double lat2, double lon1, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double r = 6371;
        return (r * c);
    }
}
