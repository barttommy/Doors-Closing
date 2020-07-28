package com.tommybart.chicagotraintracker.utils;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.tommybart.chicagotraintracker.activities.ArrivalsActivity;

public class MyLocationListener implements LocationListener {

    private static final String TAG = "MyLocationListener";
    private ArrivalsActivity arrivalsActivity;

    public MyLocationListener(ArrivalsActivity arrivalsActivity) {
        this.arrivalsActivity = arrivalsActivity;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: Location changed");
        arrivalsActivity.updateLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
