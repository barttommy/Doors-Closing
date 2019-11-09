package com.example.chicagotraintracker;

import androidx.annotation.Nullable;

public class Station {

    private String mapId;
    private String name;
    private String lat;
    private String lon;
    private double distance;

    Station(String mapId, String name, String lat, String lon) {
        this.mapId = mapId;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    String getMapId() {
        return mapId;
    }

    String getName() {
        return name;
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
        return station.mapId.equals(mapId); // mapId is unique identifier
    }
}