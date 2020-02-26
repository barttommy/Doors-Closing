package com.example.chicagotraintracker.models;

public class Train {

    private String arrivalTime;
    private String timeRemaining;
    private String latitude;
    private String longitude;

    public Train(String arrivalTime, String timeRemaining, String latitude, String longitude) {
        this.arrivalTime = arrivalTime;
        this.timeRemaining = timeRemaining;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getTimeRemaining() {
        return timeRemaining;
    }

    // TODO: Can be used for a GoogleMaps Activity
    public String getLatitude() {
        return latitude;
    }

    // TODO: Can be used for a GoogleMaps Activity
    public String getLongitude() {
        return longitude;
    }
}