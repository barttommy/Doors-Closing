package com.example.chicagotraintracker;

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
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/* TODO General:
    Get location updates instead of last known (https://developer.android.com/guide/topics/location/strategies.html#BestEstimate)
    Implement Google Maps Activity: Train locations & Station locations, show directions to station clicked on (would need more specific location)
    Extract isDelayed from API and notify user, offer implied intent to CTA's twitter for updates
    Themes: Light and Dark theme & Update dialogManager themes
    Update size of dialogs, padding?
    Change top nav bar size? Home bar button colors?
   TODO Drawer:
    Drawer menu option: Light & Dark theme switcher
    Drawer menu option: Add the about page
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final String CTA_TWITTER_NAME = "cta";
    private static final int LOCATION_REQUEST_CODE = 123;
    private static final int LOCATION_MIN_TIME = 30000;
    private static final int LOCATION_MIN_DIST = 0;
    private static final String[] DRAWER_ITEMS = {"Nearby Trains", "CTA Twitter", "About"};

    private DialogManager dialogManager;
    private boolean isLocationRequest = true;
    private LocationManager locationManager;

    private LocationListener locationListener =  new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.d(TAG, String.format("onLocationChanged: Updated Location: (%s, %s)",
                    location.getLatitude(), location.getLongitude()));
            requestedStations.clear();
            requestedStations.addAll(new LocationHandler(location).getRequestedStations());
            doRefresh();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };


    private Criteria criteria;

    private AsyncArrivalsLoader asyncTask;
    private RouteAdapter routeAdapter;
    private RecyclerView arrivalsRecycler;
    private SwipeRefreshLayout swiper;

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    private ArrayList<Route> routeList = new ArrayList<>();
    static HashMap<String, Station> stationData = new HashMap<>();
    private HashSet<Station> requestedStations = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Trains Near You");

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

        arrivalsRecycler = findViewById(R.id.arrivalsRecycler);
        routeAdapter = new RouteAdapter(routeList, this);
        arrivalsRecycler.setAdapter(routeAdapter);
        arrivalsRecycler.setLayoutManager(new LinearLayoutManager(this));
        arrivalsRecycler.addItemDecoration(new MarginItemDecoration(24));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        doRefresh();
                    }
                });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        dialogManager = new DialogManager(this);

        // TODO
        // locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        loadStationData();
        doRefresh();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!requestedStations.isEmpty()) {
            doRefresh();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (asyncTask != null) {
            asyncTask.cancel(true);
        }
        //locationManager.removeUpdates(locationListener); //TODO
    }

    private void doRefresh() {
        Log.d(TAG, "doRefresh: Refreshing data");
        swiper.setRefreshing(true);
        if (isLocationRequest) {
            setTitle("Trains Near You");
            requestedStations.clear();
            findNearestTrains();
        }
        if (connectedToNetwork() && !requestedStations.isEmpty()) {
            asyncTask = new AsyncArrivalsLoader(this, requestedStations);
            asyncTask.execute();
        } else {
            arrivalsRecycler.setVisibility(View.GONE);
            routeAdapter.notifyDataSetChanged();
            swiper.setRefreshing(false);
        }
    }

    private void findNearestTrains() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            swiper.setRefreshing(false);
        } else {
            String provider = locationManager.getBestProvider(criteria, true);
            Location currentLocation;
            if (provider != null && (currentLocation = locationManager.getLastKnownLocation(provider)) != null) {
                Log.d(TAG, String.format("Current location: %s %s", currentLocation.getLatitude(), currentLocation.getLongitude()));
                requestedStations.addAll(new LocationHandler(currentLocation).getRequestedStations());

            }
//            locationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER, LOCATION_MIN_TIME, LOCATION_MIN_DIST, locationListener);
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
        arrivalsRecycler.setVisibility(View.VISIBLE);
        swiper.setRefreshing(false);
    }

    public ArrayList<Station> search(String request) {
        ArrayList<Station> searchResult = new ArrayList<>();
        request = request.toLowerCase();
        for (Station station: stationData.values()) {
            if (station.getName().toLowerCase().contains(request)) {
                searchResult.add(station);
            }
        }
        Collections.sort(searchResult);
        return searchResult;
    }

    public void loadManualRequest(Station station) {
        setTitle(station.getName());
        isLocationRequest = false;
        requestedStations.clear();
        requestedStations.add(station);
        doRefresh();
    }

    private void loadStationData() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("CTA_Train_Database.json")));
            Instant start = Instant.now();
            DatabaseParser data = new DatabaseParser(reader);
            Instant end = Instant.now();
            Log.d(TAG, "loadStationData: Loaded in " + java.time.Duration.between(start, end));
            stationData = data.getStationData();
        } catch(Exception e) {
            e.printStackTrace();
            Log.d(TAG, "loadStationData: FATAL");
            dialogManager.showErrorDialog(R.string.error_database_title, R.string.error_database_message);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        } else if (item.getItemId() == R.id.action_search) {
            dialogManager.showInputDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void selectDrawerItem(int position) {
        String selection = DRAWER_ITEMS[position];
        switch (selection) {
            case ("Nearby Trains"):
                isLocationRequest = true;
                doRefresh();
                break;
            case ("CTA Twitter"):
                openTwitter();
                break;
            case ("About"):
                Toast.makeText(this, String.format("Selected %s!", selection), Toast.LENGTH_SHORT).show(); break;
        }
        drawerLayout.closeDrawer(drawerList);
    }

    public void openTwitter() {
        Intent intent = null;
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + CTA_TWITTER_NAME));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + CTA_TWITTER_NAME));
        }
        startActivity(intent);
    }

    boolean connectedToNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true;
                }
            }
        }
        dialogManager.showErrorDialog(R.string.error_network_title, R.string.error_network_message);
        return false;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "Implement Google Map Activity!", Toast.LENGTH_SHORT).show();
    }
}
