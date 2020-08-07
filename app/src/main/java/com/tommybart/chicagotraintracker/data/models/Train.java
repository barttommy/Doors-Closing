package com.tommybart.chicagotraintracker.data.models;

import androidx.annotation.NonNull;

import com.tommybart.chicagotraintracker.internal.TrainLine;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

public class Train {

    private long routeId;
    private int stationId;
    private int runNumber;
    private TrainLine trainLine;
    private String predictionTime;
    private String arrivalTime;
    private Boolean isApproaching;
    private Boolean isDelayed;
    private Double bearing;
    private Location location;

    public Train(long routeId, int stationId, int runNumber, TrainLine trainLine,
                 String predictionTime, String arrivalTime, Boolean isApproaching,
                 Boolean isDelayed, Double bearing, Location location) {
        this.routeId = routeId;
        this.stationId = stationId;
        this.runNumber = runNumber;
        this.trainLine = trainLine;
        this.predictionTime = predictionTime;
        this.arrivalTime = arrivalTime;
        this.isApproaching = isApproaching;
        this.isDelayed = isDelayed;
        this.bearing = bearing;
        this.location = location;
    }

    public long getRouteId() {
        return routeId;
    }

    public int getStationId() {
        return stationId;
    }

    public int getRunNumber() {
        return runNumber;
    }

    public TrainLine getTrainLine() {
        return trainLine;
    }

    public String getPredictionTime() {
        return predictionTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
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

    public Location getLocation() {
        return location;
    }

    public int getArrivalTimeMinutes() {
        return (int) Duration.between(
                LocalDateTime.parse(predictionTime),
                LocalDateTime.parse(arrivalTime)
        ).toMinutes();
    }

    public String getArrivalTimeDetail() {
        LocalDateTime parsedArrivalTime = LocalDateTime.parse(arrivalTime);
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
                ", predictionTime='" + predictionTime + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                ", isApproaching=" + isApproaching +
                ", isDelayed=" + isDelayed +
                ", bearing=" + bearing +
                ", location=" + location +
                '}';
    }
}