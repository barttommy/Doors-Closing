package com.tommybart.chicagotraintracker.data.models;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;

// TODO: Delete
public class Station implements Serializable, Comparable<Station> {

    private String mapId;
    private String name;
    private String detailedName;
    private String lat;
    private String lon;
    private HashMap<String, Boolean> trainLines;

    // Delete
    private double distance;

    private int hashCode;

    public Station(String mapId, String name, String detailedName,
                   HashMap<String, Boolean> trainLines, String lat, String lon) {
        this.mapId = mapId;
        this.name = name;
        this.detailedName = detailedName;
        this.trainLines = trainLines;
        this.lat = lat;
        this.lon = lon;
    }

    public String getMapId() {
        return mapId;
    }

    public String getName() {
        return name;
    }

    public String getDetailedName() {
        return detailedName;
    }

    public HashMap<String, Boolean> getTrainLines() {
        return trainLines;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    // TODO: Delete!
    public double getDistance() {
        return distance;
    }

    // TODO: Delete!
    public void setDistance(double distance) {
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