package com.example.chicagotraintracker;

import androidx.annotation.Nullable;

public class Station {

    private String mapId;
    private String name;
    private String lat;
    private String lon;

    public Station(String mapId, String name, String lat, String lon) {
        this.mapId = mapId;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public String getMapId() {
        return mapId;
    }

    public String getName() {
        return name;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
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