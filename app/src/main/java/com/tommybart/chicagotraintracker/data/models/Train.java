package com.tommybart.chicagotraintracker.data.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tommybart.chicagotraintracker.internal.TrainLine;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import static com.tommybart.chicagotraintracker.data.models.Route.CHICAGO_ZONE_ID;

public class Train implements Comparable<Train> {

    private int mapId;
    private int runNumber;
    private TrainLine trainLine;
    private LocalDateTime predictionDateTime;
    private LocalDateTime arrivalDateTime;
    private Boolean isApproaching;
    private Boolean isDelayed;
    private Double bearing;
    private LatLng latLng;

    public Train(int mapId, int runNumber, TrainLine trainLine,
                 LocalDateTime predictionDateTime, LocalDateTime arrivalDateTime, Boolean isApproaching,
                 Boolean isDelayed, Double bearing, LatLng latLng) {
        this.mapId = mapId;
        this.runNumber = runNumber;
        this.trainLine = trainLine;
        this.predictionDateTime = predictionDateTime;
        this.arrivalDateTime = arrivalDateTime;
        this.isApproaching = isApproaching;
        this.isDelayed = isDelayed;
        this.bearing = bearing;
        this.latLng = latLng;
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
    public LatLng getLatLng() {
        return latLng;
    }

    /*
     * Given the ability to update more frequently, comparing prediction time to arrival time
     * would give the most accurate time until arrival.
     */
    public int getArrivalTimeMinutes() {
        LocalDateTime current = ZonedDateTime.now(ZoneId.of(CHICAGO_ZONE_ID)).toLocalDateTime();
        return (int) Duration.between(current, arrivalDateTime).toMinutes();
    }

    public String getArrivalTimeDetail() {
        int hour = arrivalDateTime.getHour();
        int minutes = arrivalDateTime.getMinute();

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
                ", runNumber=" + runNumber +
                ", trainLine=" + trainLine +
                ", predictionDateTime='" + predictionDateTime + '\'' +
                ", arrivalDateTime='" + arrivalDateTime + '\'' +
                ", isApproaching=" + isApproaching +
                ", isDelayed=" + isDelayed +
                ", bearing=" + bearing +
                ", location=" + latLng +
                '}';
    }

    @Override
    public int compareTo(Train train) {
        int cmp = this.arrivalDateTime.compareTo(train.arrivalDateTime);
        if (cmp == 0) cmp = Integer.compare(this.runNumber, train.runNumber);
        return cmp;
    }
}