package com.tommybart.chicagotraintracker.data.models;

import androidx.annotation.NonNull;

import com.tommybart.chicagotraintracker.data.db.entity.Location;
import com.tommybart.chicagotraintracker.internal.TrainLine;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import static com.tommybart.chicagotraintracker.data.network.cta.CtaDeserializerKt.CHICAGO_ZONE_ID;

public class Train {

    private TrainLine trainLine;
    private ZonedDateTime arrivalTime;
    private ZonedDateTime predictionTime;
    private Boolean isApproaching;
    private Boolean isDelayed;
    private Double bearing;
    private Location location;

    public Train(TrainLine trainLine, ZonedDateTime arrivalTime, ZonedDateTime predictionTime,
                 Boolean isApproaching, Boolean isDelayed, Double bearing,
                 Location location) {
        this.trainLine = trainLine;
        this.arrivalTime = arrivalTime;
        this.predictionTime = predictionTime;
        this.isApproaching = isApproaching;
        this.isDelayed = isDelayed;
        this.bearing = bearing;
        this.location = location;
    }

    public TrainLine getTrainLine() {
        return trainLine;
    }

    public ZonedDateTime getArrivalTime() {
        return arrivalTime;
    }

    public ZonedDateTime getPredictionTime() {
        return predictionTime;
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

    // TODO - calculate with prdT vs arrT? See api documentation
    public int getArrivalTimeMinutes() {
        Instant arrivalTimeInstant = arrivalTime.toInstant();
        Instant chicagoTime = ZonedDateTime.now(ZoneId.of(CHICAGO_ZONE_ID)).toInstant();
        return (int) (Duration.between(chicagoTime, arrivalTime).toMillis() / 60000);
    }

    public String getArrivalTimeDetail() {
        int hour = arrivalTime.getHour();
        int minutes = arrivalTime.getMinute();

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

    @Override
    @NonNull
    public String toString() {
        return "Train{" +
                "trainLine=" + trainLine +
                ", arrivalTime=" + arrivalTime +
                ", predictionTime=" + predictionTime +
                ", isApproaching=" + isApproaching +
                ", isDelayed=" + isDelayed +
                ", bearing=" + bearing +
                ", location=" + location +
                '}';
    }
}