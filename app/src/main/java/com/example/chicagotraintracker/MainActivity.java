package com.example.chicagotraintracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import javax.xml.datatype.Duration;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/* TODO:
    Update currentLocation to be more accurate (https://developer.android.com/guide/topics/location/strategies.html#BestEstimate)
    Implement manual station search / selection (No location required)
    Implement Google Maps Activity
    Extract isDelayed from API and notify user, offer implied intent to CTA's twitter for updates
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int LOCATION_REQUEST_CODE = 123;

    private LocationManager locationManager;
    private Criteria criteria;

    private AsyncArrivalsLoader asyncTask;

    private RouteAdapter routeAdapter;
    private SwipeRefreshLayout swiper;

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private String[] drawerItems;

    private ArrayList<Route> routeList = new ArrayList<>();
    static HashMap<String, Station> stationData = new HashMap<>();
    private HashSet<Station> requestedStations = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Trains Near You"); //TODO Update title based on request (near you or specific station from manual selection)

        // TODO - Bogus data for now
        drawerItems = new String[5];
        for (int i = 0; i < drawerItems.length; i++)
            drawerItems[i] = "Drawer Item " + (i+1);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, drawerItems));
        drawerList.setOnItemClickListener(
                new ListView.OnItemClickListener(){
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
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doRefresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        asyncTask.cancel(true);
    }

    private void doRefresh() {
        Log.d(TAG, "doRefresh: Refreshing data");
        swiper.setRefreshing(true);
        requestedStations.clear();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            swiper.setRefreshing(false);
        } else {

            String provider = locationManager.getBestProvider(criteria, true);
            Location currentLocation = locationManager.getLastKnownLocation(provider);
            Log.d(TAG, String.format("Current location: %s %s", currentLocation.getLatitude(), currentLocation.getLongitude()));

            requestedStations.addAll(new LocationHandler(currentLocation).getRequestedStations());

            if (connectedToNetwork()) {
                asyncTask = new AsyncArrivalsLoader(this, requestedStations);
                asyncTask.execute();
            } else {
                swiper.setRefreshing(false);
            }
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
        requestedStations.clear();
        routeList.clear();
        routeList.addAll(results);
        Collections.sort(routeList);
        routeAdapter.notifyDataSetChanged();
        swiper.setRefreshing(false);
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
        }
        switch(item.getItemId()) {
            case R.id.search_menu_item:
                Toast.makeText(this, "Search Pressed", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void selectDrawerItem(int position) {
        Toast.makeText(this, String.format("Selected %s", drawerItems[position]), Toast.LENGTH_SHORT).show();
        drawerLayout.closeDrawer(drawerList);
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
            showErrorDialog("Unable to load stations", "Error occurred when parsing CTA station database");
        }
    }

    private void showErrorDialog(String title, String subtitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }
        );

        builder.setTitle(title);
        builder.setMessage(subtitle);

        AlertDialog dialog = builder.create();
        dialog.show();
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
        showErrorDialog("No Network Connection", "Train arrivals cannot be viewed without a network connection");
        return false;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "Implement Google Map Activity!", Toast.LENGTH_SHORT).show();
    }
}
