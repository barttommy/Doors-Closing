package com.example.chicagotraintracker;

public class Train {

    private String arrivalTime;
    private String timeRemaining;
    private String latitude;
    private String longitude;

    Train(String arrivalTime, String timeRemaining, String latitude, String longitude) {
        this.arrivalTime = arrivalTime;
        this.timeRemaining = timeRemaining;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    String getArrivalTime() {
        return arrivalTime;
    }

    String getTimeRemaining() {
        return timeRemaining;
    }

    String getLatitude() {
        return latitude;
    }

    String getLongitude() {
        return longitude;
    }
}