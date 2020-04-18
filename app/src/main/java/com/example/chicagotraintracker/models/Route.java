package com.example.chicagotraintracker.models;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * A Route stores a collection of incoming Trains that share the same line, station, and destination
 */
public class Route implements Comparable<Route> {

    public static final int ROUTE_TRAIN_LIMIT = 3;

    // Train line strings as they appear in the api
    public static final String BROWN_LINE = "Brn";
    public static final String PURPLE_LINE = "P";
    public static final String RED_LINE = "Red";
    public static final String BLUE_LINE = "Blue";
    public static final String GREEN_LINE = "G";
    public static final String ORANGE_LINE = "Org";
    public static final String PINK_LINE = "Pink";
    public static final String YELLOW_LINE = "Y";

    public static final String[] TRAIN_LINES = {
            Route.RED_LINE, Route.BLUE_LINE, Route.GREEN_LINE, Route.BROWN_LINE,
            Route.PURPLE_LINE, Route.YELLOW_LINE, Route.PINK_LINE, Route.ORANGE_LINE
    };

    private String line;
    private String stationId;
    private String stationName;
    private String destination;
    private ArrayList<Train> trains;
    private int hashCode;

    public Route (String line, String stationId, String stationName,
                  String destination, ArrayList<Train> trains) {
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
        return this.line.equals(route.getLine())
                && this.stationId.equals(route.getStationId())
                && this.destination.equals(route.getDestination());
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
