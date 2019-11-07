package com.example.chicagotraintracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private RouteAdapter routeAdapter;
    private SwipeRefreshLayout swiper;

    private ArrayList<Route> routeList = new ArrayList<>();
    private Station requestedStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.arrivalsRecycler);
        routeAdapter = new RouteAdapter(routeList, this);
        recyclerView.setAdapter(routeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        doRefresh();
                    }
                });

        requestedStation = new Station("40380", "Clark/Lake", "", "");

        generateBogusData();
    }

    private void generateBogusData() {
        ArrayList<Train> trains = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            trains.add(new Train("Arriving at " + i, i+"", "", ""));
        }
        routeList.add(new Route("Brn", "40380", "Clark/Lake","Kimball", trains));
    }

    private void doRefresh() {
        Log.d(TAG, "doRefresh: Refreshing data");
        swiper.setRefreshing(false);
    }

    void acceptResults(ArrayList<Route> results) {
        routeList.clear();
        routeList.addAll(results);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "Implement Map!", Toast.LENGTH_SHORT).show();
    }
}
