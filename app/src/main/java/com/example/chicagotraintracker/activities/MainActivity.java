package com.example.chicagotraintracker.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.chicagotraintracker.utils.DatabaseParser;
import com.example.chicagotraintracker.utils.DialogManager;
import com.example.chicagotraintracker.adapters.DrawerAdapter;
import com.example.chicagotraintracker.utils.LocationHandler;
import com.example.chicagotraintracker.adapters.MarginItemDecoration;
import com.example.chicagotraintracker.utils.MyLocationListener;
import com.example.chicagotraintracker.R;
import com.example.chicagotraintracker.adapters.RouteAdapter;
import com.example.chicagotraintracker.models.Route;
import com.example.chicagotraintracker.models.Station;
import com.example.chicagotraintracker.asyncs.AsyncArrivalsLoader;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String CTA_TWITTER_NAME = "cta";
    private static final String LOCATION_APP_TITLE = "Trains Near You";
    private static final int LOCATION_REQUEST_CODE = 123;
    private static final int LOCATION_MIN_TIME = 10 * 1000;
    private static final int LOCATION_MIN_DIST = 500;
    private static final int SEARCH_CODE = 585;
    private static final String[] DRAWER_ITEMS = {"Nearby Trains", "CTA Twitter", "About"};

    private DialogManager dialogManager;

    private boolean requestingLocation = true;
    private LocationManager locationManager;
    private MyLocationListener locationListener;
    private LocationHandler locationHandler;
    private FusedLocationProviderClient mFusedLocationClient;

    private AsyncArrivalsLoader asyncArrivalsLoader;
    private RouteAdapter routeAdapter;
    private RecyclerView arrivalsRecycler;
    private SwipeRefreshLayout swiper;

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    private ArrayList<Route> routeList = new ArrayList<>();
    public static HashMap<String, Station> stationData = new HashMap<>();
    private HashSet<Station> requestedStations = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrivalsRecycler = findViewById(R.id.arrivals_recycler);
        routeAdapter = new RouteAdapter(routeList, this);
        arrivalsRecycler.setAdapter(routeAdapter);
        arrivalsRecycler.setLayoutManager(new LinearLayoutManager(this));
        arrivalsRecycler.addItemDecoration(new MarginItemDecoration(24));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        locationHandler = new LocationHandler();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        dialogManager = new DialogManager(this);

        setTitle(LOCATION_APP_TITLE);
        setupDrawer();
        loadStationData();

        if (!checkLocationPermission()) {
            requestLocationPermission();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
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
        cancelAsync(asyncArrivalsLoader);
    }

    private void doRefresh() {
        Log.d(TAG, "doRefresh: Reloading data");
        swiper.setRefreshing(true);
        if (connectedToNetwork() && !requestedStations.isEmpty()) {
            cancelAsync(asyncArrivalsLoader);
            asyncArrivalsLoader = new AsyncArrivalsLoader(this, requestedStations);
            asyncArrivalsLoader.execute();
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
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, SEARCH_CODE);
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

    private void loadStationData() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("CTA_Train_Database.json")));
            DatabaseParser data = new DatabaseParser(reader);
            stationData = data.getStationData();
        } catch(Exception e) {
            e.printStackTrace();
            Log.d(TAG, "loadStationData: FATAL");
            dialogManager.showErrorDialog(
                    R.string.error_database_title,
                    R.string.error_database_message);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
        } else if (item.getItemId() == R.id.action_search) {
            startSearchActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void selectDrawerItem(int position) {
        switch(position) {
            case 0:
                requestingLocation = true;
                setTitle(LOCATION_APP_TITLE);
                setupLocationServices();
                break;
            case 1:
                openTwitter();
                break;
            case 2:
                startAboutActivity();
                break;
        }
        drawerLayout.closeDrawer(drawerList);
    }

    private void setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.left_drawer);
        drawerList.setAdapter(new DrawerAdapter(DRAWER_ITEMS, this));
        drawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectDrawerItem(position);
                    }
                }
        );
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
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
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            locationHandler.setLocation(location);
                            requestedStations.clear();
                            requestedStations.addAll(locationHandler.getRequestedStations());
                            Log.d(TAG, "onSuccess: loading stations at last known location");
                            doRefresh();
                        } else {
                            Log.d(TAG, "onSuccess: Last known location is null");
                        }
                    }
                });
    }

    // MyLocationListener update callback
    public void updateLocation(Location location) {
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
        requestedStations.clear();
        routeAdapter.notifyDataSetChanged();
    }

    private void stopLocationRequests() {
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
