package com.tommybart.chicagotraintracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tommybart.chicagotraintracker.R;
import com.tommybart.chicagotraintracker.adapters.MarginItemDecoration;
import com.tommybart.chicagotraintracker.adapters.SearchAdapter;
import com.tommybart.chicagotraintracker.data.models.Station;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.ContentValues.TAG;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView searchRecycler;
    private SearchAdapter searchAdapter;
    private ArrayList<Station> searchResults = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchRecycler = findViewById(R.id.search_recycler);
        searchResults.addAll(ArrivalsActivity.stationData.values());
        searchAdapter = new SearchAdapter(searchResults, this);
        searchRecycler.setAdapter(searchAdapter);
        searchRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchRecycler.addItemDecoration(new MarginItemDecoration(24));

        setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint("Search stations");
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setIconifiedByDefault(false);
        searchView.requestFocusFromTouch();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                doFilter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void doFilter(String query) {
        searchResults.clear();
        query = query.toLowerCase();
        for (Station station: ArrivalsActivity.stationData.values()) {
            if (station.getName().toLowerCase().contains(query)) {
                searchResults.add(station);
            }
        }
        Collections.sort(searchResults);
        searchAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {
        int position = searchRecycler.getChildLayoutPosition(v);
        Station selected = searchResults.get(position);
        sendResult(selected);
    }

    private void sendResult(Station selected) {
        Intent data = new Intent(this, ArrivalsActivity.class);
        data.putExtra("STATION", selected);
        setResult(RESULT_OK, data);
        finish();
    }
}