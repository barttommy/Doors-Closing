package com.tommybart.chicagotraintracker.data.models;

import java.io.Serializable;
import java.util.Objects;

public class Station implements Comparable<Station>, Serializable {

    private int mapId;
    private String name;
    private String detailedName;
    private Boolean isAccessible;
    private AvailableTrainLines availableTrainLines;
    private LatLng latLng;

    public Station(int mapId, String name, String detailedName, Boolean isAccessible,
                   AvailableTrainLines availableTrainLines, LatLng latLng) {
        this.mapId = mapId;
        this.name = name;
        this.detailedName = detailedName;
        this.isAccessible = isAccessible;
        this.availableTrainLines = availableTrainLines;
        this.latLng = latLng;
    }

    public int getMapId() {
        return mapId;
    }

    public String getName() {
        return name;
    }

    public String getDetailedName() {
        return detailedName;
    }

    public Boolean getIsAccessible() {
        return isAccessible;
    }

    public AvailableTrainLines getAvailableTrainLines() {
        return availableTrainLines;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return mapId == station.mapId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mapId);
    }

    @Override
    public int compareTo(Station s) {
        int cmp = this.name.compareTo(s.getName());
        if (cmp == 0) cmp = this.detailedName.compareTo(s.getDetailedName());
        if (cmp == 0) cmp = Integer.compare(this.mapId, s.getMapId());
        return cmp;
    }
}