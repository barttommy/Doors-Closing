package com.tommybart.chicagotraintracker.data.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tommybart.chicagotraintracker.internal.TrainLine;

import java.util.ArrayList;

/**
 * A Route stores a collection of incoming Trains at a station that share the same line and
 * destination
 */
public class Route implements Comparable<Route>{

    public static final int TRAIN_LIMIT = 3;
    public static final String CHICAGO_ZONE_ID = "America/Chicago";

    private int mapId;
    private String stationName;
    private String destinationName;
    private TrainLine trainLine;
    private ArrayList<Train> arrivals;

    public Route (int mapId, String stationName, String destinationName,
                  TrainLine trainLine, ArrayList<Train> arrivals) {
        this.mapId = mapId;
        this.stationName = stationName;
        this.destinationName = destinationName;
        this.trainLine = trainLine;
        this.arrivals = arrivals;
    }

    private int getMapId() {
        return mapId;
    }

    public String getStationName() {
        return stationName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public TrainLine getTrainLine() {
        return trainLine;
    }

    public ArrayList<Train> getArrivals() {
        return arrivals;
    }

    @Override
    public boolean equals(@Nullable Object that) {
        if (that == this) return true;
        else if (!(that instanceof Route)) return false;
        Route route = (Route) that;
        return this.mapId == route.getMapId()
                && this.destinationName.equals(route.getDestinationName())
                && this.trainLine == route.getTrainLine();
    }

    private int hashCode;
    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = 17;
            hashCode = 37 * hashCode + trainLine.hashCode();
            hashCode = 37 * hashCode + mapId;
            hashCode = 37 * hashCode + destinationName.hashCode();
        }
        return hashCode;
    }

    @Override
    public int compareTo(Route route) {
        int cmp = Integer.compare(this.mapId, route.getMapId());
        if (cmp == 0) cmp = this.trainLine.compareTo(route.getTrainLine());
        if (cmp == 0) cmp = this.destinationName.compareTo(route.getDestinationName());
        return cmp;
    }

    @NonNull
    @Override
    public String toString() {
        return "Route{" +
                "mapId=" + mapId +
                ", stationName='" + stationName + '\'' +
                ", destinationName='" + destinationName + '\'' +
                ", trainLine=" + trainLine +
                ", arrivals=" + arrivals +
                '}';
    }
}
