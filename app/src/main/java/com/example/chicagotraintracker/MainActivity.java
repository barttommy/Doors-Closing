package com.example.chicagotraintracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


/* TODO:
    Update request algorithm
    Build nicer looking cells w/ padding & background
    Implement network check before async execute
    Clean CSV station names and repeated mapId's
    Implement manual station search / selection (No location required)
    Implement Google Maps Activity
    Extract isDelayed from API and notify user, offer implied intent to CTA's twitter for updates
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private static final int LOCATION_REQUEST_CODE = 123;
    private LocationManager locationManager;
    private Criteria criteria;

    private RouteAdapter routeAdapter;
    private SwipeRefreshLayout swiper;

    private ArrayList<Route> routeList = new ArrayList<>();
    private ArrayList<Station> stationList = new ArrayList<>();
    private ArrayList<Station> requestedStations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.arrivalsRecycler);
        routeAdapter = new RouteAdapter(routeList, this);
        recyclerView.setAdapter(routeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addItemDecoration(new MarginItemDecoration(24));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        doRefresh();
                    }
                });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        loadStationData();
        doRefresh();
    }

    private void doRefresh() {
        Log.d(TAG, "doRefresh: Refreshing data");
        swiper.setRefreshing(true);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            swiper.setRefreshing(false);
        } else {
            String provider = locationManager.getBestProvider(criteria, true);
            Location currentLocation = locationManager.getLastKnownLocation(provider);

            getNearbyStations(currentLocation);
            Log.d(TAG, String.format("Current location: %s %s", currentLocation.getLatitude(), currentLocation.getLongitude()));

            // size > 0, internet connection
            new AsyncArrivalsLoader(this, requestedStations).execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PERMISSION_GRANTED) {
                doRefresh();
            }
        }
    }

    void acceptResults(ArrayList<Route> results) {
        routeList.clear();
        routeList.addAll(results);
        Collections.sort(routeList);
        routeAdapter.notifyDataSetChanged();
        swiper.setRefreshing(false);
    }

    private void getNearbyStations(Location currentLocation) {
        try {
            for (Station station: stationList) {
                double lon = Double.parseDouble(station.getLon());
                double lat = Double.parseDouble(station.getLat());
                double distance = distanceBetweenCoords(currentLocation.getLatitude(), lat, currentLocation.getLongitude(), lon);
                if (distance <= 0.8) { // 0.8 km = approx 0.5 mile range
                    if (!requestedStations.contains(station)) { //TODO Remove on csv update
                        station.setDistance(distance);
                        requestedStations.add(station);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getNearbyStations: FAILED");
        }
    }

    // Haversine formula to calculate distance between coordinates
    private double distanceBetweenCoords(double lat1, double lat2, double lon1, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double r = 6371;
        return (r * c);
    }

    // TODO check network function

    private void loadStationData() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("CTA_-_System_Information_-_List_of__L__Stops.csv")));
            DataParser data = new DataParser(reader);
            for (int i = 0; i < data.Map_ID.length; i++) {
                Station s = new Station(data.Map_ID[i], data.Station_Name[i], data.Location_X[i], data.Location_Y[i]);
                if (!stationList.contains(s)) { //TODO Remove on csv update
                    stationList.add(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "Implement Google Map Activity!", Toast.LENGTH_SHORT).show();
    }
}
