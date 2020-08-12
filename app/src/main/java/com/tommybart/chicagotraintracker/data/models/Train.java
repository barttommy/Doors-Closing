package com.tommybart.chicagotraintracker.data.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tommybart.chicagotraintracker.internal.TrainLine;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

public class Train {

    private long routeId;
    private int mapId;
    private int runNumber;
    private TrainLine trainLine;
    private LocalDateTime predictionDateTime;
    private LocalDateTime arrivalDateTime;
    private Boolean isApproaching;
    private Boolean isDelayed;
    private Double bearing;
    private Location location;

    public Train(long routeId, int mapId, int runNumber, TrainLine trainLine,
                 LocalDateTime predictionDateTime, LocalDateTime arrivalDateTime, Boolean isApproaching,
                 Boolean isDelayed, Double bearing, Location location) {
        this.routeId = routeId;
        this.mapId = mapId;
        this.runNumber = runNumber;
        this.trainLine = trainLine;
        this.predictionDateTime = predictionDateTime;
        this.arrivalDateTime = arrivalDateTime;
        this.isApproaching = isApproaching;
        this.isDelayed = isDelayed;
        this.bearing = bearing;
        this.location = location;
    }

    public long getRouteId() {
        return routeId;
    }

    public int getMapId() {
        return mapId;
    }

    public int getRunNumber() {
        return runNumber;
    }

    public TrainLine getTrainLine() {
        return trainLine;
    }

    public LocalDateTime getPredictionDateTime() {
        return predictionDateTime;
    }

    public LocalDateTime getArrivalDateTime() {
        return arrivalDateTime;
    }

    public Boolean isApproaching() {
        return isApproaching;
    }

    public Boolean isDelayed() {
        return isDelayed;
    }

    public Double getBearing() {
        return bearing;
    }

    @Nullable
    public Location getLocation() {
        return location;
    }

    public int getArrivalTimeMinutes() {
        return (int) Duration.between(predictionDateTime, arrivalDateTime).toMinutes();
    }

    public String getArrivalTimeDetail() {
        LocalDateTime parsedArrivalTime = arrivalDateTime;
        int hour = parsedArrivalTime.getHour();
        int minutes = parsedArrivalTime.getMinute();

        String meridiem = "am";
        if (hour > 12) {
            hour = hour - 12;
            meridiem = "pm";
        } else if (hour == 12) {
            meridiem = "pm";
        } else if (hour == 0) {
            hour = 12;
        }

        String minutesString = (minutes < 10) ? "0" + minutes : Integer.toString(minutes);
        return String.format("Arriving at %s:%s %s", hour, minutesString, meridiem);
    }

    @NonNull
    @Override
    public String toString() {
        return "Train{" +
                "routeId=" + routeId +
                ", runNumber=" + runNumber +
                ", trainLine=" + trainLine +
                ", predictionDateTime='" + predictionDateTime + '\'' +
                ", arrivalDateTime='" + arrivalDateTime + '\'' +
                ", isApproaching=" + isApproaching +
                ", isDelayed=" + isDelayed +
                ", bearing=" + bearing +
                ", location=" + location +
                '}';
    }
}