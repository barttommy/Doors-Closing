package com.example.chicagotraintracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chicagotraintracker.R;
import com.example.chicagotraintracker.activities.MainActivity;
import com.example.chicagotraintracker.models.Station;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {

    private ArrayList<Station> stationList = new ArrayList<>();
    private MainActivity mainActivity;

    public SearchAdapter(ArrayList<Station> stationList, MainActivity mainActivity) {
        this.stationList = stationList;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_row_item, parent, false);
        return new SearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {

        // TODO

    }

    @Override
    public int getItemCount() {
        return stationList.size();
    }
}
