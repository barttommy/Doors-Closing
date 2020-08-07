package com.tommybart.chicagotraintracker.data.models;

public class Station implements Comparable<Station> {

    private String stationId;
    private String name;
    private String detailedName;
    private AvailableTrainLines availableTrainLines;
    private Location location;

    public Station(String stationId, String name, String detailedName,
                   AvailableTrainLines availableTrainLines, Location location) {
        this.stationId = stationId;
        this.name = name;
        this.detailedName = detailedName;
        this.availableTrainLines = availableTrainLines;
        this.location = location;
    }

    public String getStationId() {
        return stationId;
    }

    public String getName() {
        return name;
    }

    public String getDetailedName() {
        return detailedName;
    }

    public AvailableTrainLines getAvailableTrainLines() {
        return availableTrainLines;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public int compareTo(Station s) {
        int cmp = this.name.compareTo(s.getName());
        if (cmp == 0) cmp = this.detailedName.compareTo(s.getDetailedName());
        if (cmp == 0) cmp = this.stationId.compareTo(s.getStationId());
        return cmp;
    }
}