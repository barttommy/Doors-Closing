package com.tommybart.chicagotraintracker.utils;

import android.location.Location;
import android.util.Log;

import com.tommybart.chicagotraintracker.models.Route;
import com.tommybart.chicagotraintracker.models.Station;
import com.tommybart.chicagotraintracker.activities.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class LocationHandler {

    private static final String TAG = "LocationHandler";
    private static final double LOCATION_REQUEST_RANGE_KM = 1.60934; // One mile

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
     * Update linesInRange with all stations within LOCATION_REQUEST_RANGE_KM of user location
     */
    private void getNearbyStations() {
        try {
            for (Station station: MainActivity.stationData.values()) {
                double lon = Double.parseDouble(station.getLon());
                double lat = Double.parseDouble(station.getLat());
                double distance = getDistance(
                        currentLocation.getLatitude(), lat, currentLocation.getLongitude(), lon);
                if (distance <= LOCATION_REQUEST_RANGE_KM) {
                    station.setDistance(distance);
                    insertByDistance(station);
                    linesInRange = getMapDisjunction(linesInRange, station.getTrainLines());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getNearbyStations: FAILED");
        }
    }

    /*
     * Update requestedStations with the closest stations that satisfy the available lines in the
     * area.
     */
    private void requestBestStations() {
        HashMap<String, Boolean> map = newLinesInRange();
        for (int i = 0; i < stationsInRange.size(); i++) {
            if (map.equals(linesInRange)) return;
            Station station = stationsInRange.get(i);
            HashMap<String, Boolean> old = new HashMap<>(map);
            map = getMapDisjunction(map, station.getTrainLines());
            if (!old.equals(map)) {
                requestedStations.add(station);
            }
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
        String[] trainLines = Route.TRAIN_LINES;
        for (String line: trainLines) {
            map.put(line, false);
        }
        return map;
    }

    private HashMap<String, Boolean> getMapDisjunction(HashMap<String, Boolean> map1,
                                                       HashMap<String, Boolean> map2) {
        HashMap<String, Boolean> result = new HashMap<>(map1);
        try {
            for (String key: map1.keySet()) {
                result.put(key, map1.get(key) || map2.get(key));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.d(TAG, "updateLinesInRange: Unboxing exception");
        }
        return result;
    }

    // Haversine formula to calculate distance between coordinates
    private double getDistance(double lat1, double lat2, double lon1, double lon2) {
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
