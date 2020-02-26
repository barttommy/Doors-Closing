package com.example.chicagotraintracker.models;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Stores all Trains for a specific Route
 *
 * A Route stores a collection of incoming Trains that share the same line, stationId, and destination
 */
public class Route implements Comparable<Route> {

    private String line;
    private String stationId;
    private String stationName;
    private String destination;
    private ArrayList<Train> trains;
    private int hashCode;

    public Route (String line, String stationId, String stationName, String destination, ArrayList<Train> trains) {
        this.line = line;
        this.stationId = stationId;
        this.stationName = stationName;
        this.destination = destination;
        this.trains = trains;
    }

    public String getLine() {
        return line;
    }

    private String getStationId() {
        return stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public String getDestination() {
        return destination;
    }

    public ArrayList<Train> getTrains() {
        return trains;
    }

    @Override
    public boolean equals(@Nullable Object that) {
        if (that == this) return true;
        else if (!(that instanceof Route)) return false;
        Route route = (Route) that;
        return this.line.equals(route.getLine()) &&
                this.stationId.equals(route.getStationId()) &&
                this.destination.equals(route.getDestination());
    }

    @Override
    public int compareTo(Route route) {
        int cmp = this.getStationId().compareTo(route.getStationId());
        if (cmp == 0) cmp = this.getLine().compareTo(route.getLine());
        if (cmp == 0) cmp = this.getDestination().compareTo(route.getDestination());
        return cmp;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = 17;
            hashCode = 37 * hashCode + line.hashCode();
            hashCode = 37 * hashCode + stationId.hashCode();
            hashCode = 37 * hashCode + destination.hashCode();
        }
        return hashCode;
    }
}
