package com.example.chicagotraintracker;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class Station implements Comparable<Station> {

    private String mapId;
    private String name;
    private String detailedName;
    private String lat;
    private String lon;
    private HashMap<String, Boolean> trainLines;
    private double distance;

    private int hashCode;

    Station(String mapId, String name, String detailedName, HashMap<String, Boolean> trainLines, String lat, String lon) {
        this.mapId = mapId;
        this.name = name;
        this.detailedName = detailedName;
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

    String getDetailedName() {
        return detailedName;
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
        return station.mapId.equals(mapId) &&
                station.name.equals(name) &&
                station.trainLines.equals(trainLines);
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = 17;
            hashCode = 37 * hashCode + mapId.hashCode();
            hashCode = 37 * hashCode + name.hashCode();
            hashCode = 37 * hashCode + trainLines.hashCode();
        }
        return hashCode;
    }

    @Override
    public int compareTo(Station s) {
        int cmp = this.name.compareTo(s.getName());
        if (cmp == 0) cmp = this.detailedName.compareTo(s.getDetailedName());
        if (cmp == 0) cmp = this.mapId.compareTo(s.getMapId());
        return cmp;
    }
}