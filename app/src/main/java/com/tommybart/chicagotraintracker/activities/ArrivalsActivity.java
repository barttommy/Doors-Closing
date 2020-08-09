package com.tommybart.chicagotraintracker.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.tommybart.chicagotraintracker.R;
import com.tommybart.chicagotraintracker.adapters.MarginItemDecoration;
import com.tommybart.chicagotraintracker.adapters.RouteAdapter;
import com.tommybart.chicagotraintracker.data.models.Route;
import com.tommybart.chicagotraintracker.data.models.Station;
import com.tommybart.chicagotraintracker.utils.LocationHandler;
import com.tommybart.chicagotraintracker.utils.MyLocationListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/*
 * In order to run, the app must have an API key in AsyncArrivalLoader.java
 * A public test key can be found here:
 * https://www.transitchicago.com/developers/traintracker/testkey/
 */
public class ArrivalsActivity extends AppCompatActivity {

    private static final String TAG = "ArrivalsActivity";
    private static final String CTA_TWITTER_NAME = "cta";
    private static final String LOCATION_APP_TITLE = "Trains Near You";
    private static final int LOCATION_MIN_TIME = 10 * 1000;
    private static final int LOCATION_MIN_DIST = 500;
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int SEARCH_CODE = 200;
    private static final String[] DRAWER_ITEMS = {"Nearby Trains", "CTA Twitter", "About"};

    private boolean requestingLocation = true;
    private LocationManager locationManager;
    private MyLocationListener locationListener;
    private LocationHandler locationHandler;
    private FusedLocationProviderClient mFusedLocationClient;

    //private AsyncArrivalsLoader asyncArrivalsLoader;
    private RouteAdapter routeAdapter;
    private RecyclerView arrivalsRecycler;
    private SwipeRefreshLayout swiper;

    private ArrayList<Route> routeList = new ArrayList<>();
    public static HashMap<String, Station> stationData = new HashMap<>();
    private HashSet<Station> requestedStations = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrivals);

        arrivalsRecycler = findViewById(R.id.arrivals_recycler);
        routeAdapter = new RouteAdapter(routeList, this);
        arrivalsRecycler.setAdapter(routeAdapter);
        arrivalsRecycler.setLayoutManager(new LinearLayoutManager(this));
        arrivalsRecycler.addItemDecoration(new MarginItemDecoration(24));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(this::doRefresh);

        locationHandler = new LocationHandler();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setTitle(LOCATION_APP_TITLE);

        if (!checkLocationPermission()) {
            requestLocationPermission();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!requestedStations.isEmpty() && !requestingLocation) {
            doRefresh();
        } else if (requestingLocation) {
            setupLocationServices();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationRequests();
        //cancelAsync(asyncArrivalsLoader);
    }

    private void doRefresh() {
        Log.d(TAG, "doRefresh: Reloading data");
        swiper.setRefreshing(true);
        if (connectedToNetwork() && !requestedStations.isEmpty()) {
            //cancelAsync(asyncArrivalsLoader);
            //asyncArrivalsLoader = new AsyncArrivalsLoader(this, requestedStations);
            //asyncArrivalsLoader.execute();
        } else {
            showNearbyTrainsError();
            swiper.setRefreshing(false);
        }
    }

    // AsyncArrivalsLoader Callback
    public void acceptResults(ArrayList<Route> results) {
        routeList.clear();
        routeList.addAll(results);
        Collections.sort(routeList);
        routeAdapter.notifyDataSetChanged();
        swiper.setRefreshing(false);
        if (!routeList.isEmpty()) {
            arrivalsRecycler.setVisibility(View.VISIBLE);
        }
    }

    private void cancelAsync(AsyncTask async) {
        if (async != null && !async.isCancelled()) {
            Log.d(TAG, "cancelAsync: Canceling previous task");
            async.cancel(true);
        }
    }

    private void startSearchActivity() {
//        Intent intent = new Intent(this, SearchActivity.class);
//        startActivityForResult(intent, SEARCH_CODE);
    }

    private void startAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_CODE && resultCode == RESULT_OK && data != null) {
            if (data.hasExtra("STATION")) {
                Station station = (Station) data.getSerializableExtra("STATION");
                if (station != null) {
                    loadManualRequest(station);
                }
            }
        }
    }

    public void loadManualRequest(Station station) {
        Log.d(TAG, "loadManualRequest: for station: " + station.getDetailedName());
        setTitle(station.getName());
        requestingLocation = false;
        stopLocationRequests();
        requestedStations.clear();
        requestedStations.add(station);
        doRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            startSearchActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    public void openTwitter() {
        Intent intent;
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("twitter://user?screen_name=" + CTA_TWITTER_NAME));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/" + CTA_TWITTER_NAME));
        }
        startActivity(intent);
    }

    private boolean connectedToNetwork() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
            }
        }
        return false;
    }

    /*
     * Location setup and permissions
     */

    private void setupLocationServices() {
        if (checkLocationPermission() && requestingLocation) {
            if (locationManager != null && locationListener != null) {
                requestLocationUpdates();
            } else if (locationManager == null && locationListener == null) {
                startLocationListener();
            }
        } else {
            showNearbyTrainsError();
        }
    }

    private void startLocationListener() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener(this);
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        getLastKnownLocation();
        if (checkLocationPermission() && locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    LOCATION_MIN_TIME, LOCATION_MIN_DIST, locationListener);
        }
    }

    private void getLastKnownLocation() {
        if (!checkLocationPermission()) return;
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        locationHandler.setLocation(location);
                        requestedStations.clear();
                        requestedStations.addAll(locationHandler.getRequestedStations());
                        Log.d(TAG, "onSuccess: loading stations at last known location");
                        doRefresh();
                    } else {
                        Log.d(TAG, "onSuccess: Last known location is null");
                        showNearbyTrainsError();
                    }
                });
    }

    // MyLocationListener update callback
    public void updateLocation(Location location) {
        if (location == null) {
            Log.d(TAG, "updateLocation: location is null");
            showNearbyTrainsError();
            return;
        }
        Log.d(TAG, String.format("updateLocation: Adding routes at location %.4f %.4f",
                location.getLatitude(), location.getLongitude()));
        locationHandler.setLocation(location);
        requestedStations.clear();
        requestedStations.addAll(locationHandler.getRequestedStations());
        doRefresh();
    }

    private boolean checkLocationPermission() {
        return ContextCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                { Manifest.permission.ACCESS_FINE_LOCATION }, LOCATION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult
            (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)
                    && grantResults[0] == PERMISSION_GRANTED) {
                setupLocationServices();
            }
        }
    }

    private void showNearbyTrainsError() {
        arrivalsRecycler.setVisibility(View.GONE);
        routeList.clear();
        if (requestingLocation) {
            requestedStations.clear();
        }
        routeAdapter.notifyDataSetChanged();
    }

    private void stopLocationRequests() {
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
