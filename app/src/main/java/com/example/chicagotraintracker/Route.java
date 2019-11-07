package com.example.chicagotraintracker;

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
    private String hexColor;
    private ArrayList<Train> trains;

    private int hashCode;

    Route (String line, String stationId, String stationName, String destination, ArrayList<Train> trains) {
        // TODO: Extract to colors to adapter? What is the best practice here? Colors.xml?
        switch(line) {
            case ("Brn"):
                hexColor = "#b38054";
                break;
            case ("P"):
                hexColor = "#800080";
                break;
            case ("Red"):
                hexColor = "#ed2b15";
                break;
            case ("Blue"):
                hexColor = "#3E86F5";
                break;
            case ("G"):
                hexColor = "#34B13C";
                break;
            case ("Org"):
                hexColor = "#FFA200";
                break;
            case ("Pink"):
                hexColor = "#F19CF5";
                break;
            case ("Y"):
                hexColor = "#F1EE17";
                break;
            default:
                hexColor = "#FFFFFF";
                break;
        }
        this.line = line;
        this.stationId = stationId;
        this.stationName = stationName;
        this.destination = destination;
        this.trains = trains;
    }

    String getLine() {
        return line;
    }

    String getStationId() {
        return stationId;
    }

    String getStationName() {
        return stationName;
    }

    String getDestination() {
        return destination;
    }

    String getHexColor() {
        return hexColor;
    }

    ArrayList<Train> getTrains() {
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
