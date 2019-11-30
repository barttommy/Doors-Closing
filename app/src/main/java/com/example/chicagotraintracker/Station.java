package com.example.chicagotraintracker;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class Station {

    private String mapId;
    private String name;
    private String lat;
    private String lon;
    private HashMap<String, Boolean> trainLines;
    private double distance;

    private int hashCode;

    Station(String mapId, String name, HashMap<String, Boolean> trainLines, String lat, String lon) {
        this.mapId = mapId;
        this.name = name;
        this.trainLines = trainLines;
        this.lat = lat;
        this.lon = lon;
    }

    String getMapId() {
        return mapId;
    }

    String getName() {
        return name;
    }

    HashMap<String, Boolean> getTrainLines() {
        return trainLines;
    }

    String getLat() {
        return lat;
    }

    String getLon() {
        return lon;
    }

    double getDistance() {
        return distance;
    }

    void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(@Nullable Object that) {
        if (that == this) {
            return true;
        } else if (!(that instanceof Station)) {
            return false;
        }
        Station station = (Station) that;
        return station.mapId.equals(mapId);
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = 17;
            hashCode = 37 * hashCode + mapId.hashCode();
        }
        return hashCode;
    }
}