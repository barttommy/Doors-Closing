package com.tommybart.chicagotraintracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tommybart.chicagotraintracker.R;
import com.tommybart.chicagotraintracker.activities.SearchActivity;
import com.tommybart.chicagotraintracker.data.models.Route;
import com.tommybart.chicagotraintracker.data.models.Station;
import com.tommybart.chicagotraintracker.internal.TrainLine;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {

    private SearchActivity searchActivity;
    private ArrayList<Station> searchResults;

    public SearchAdapter(ArrayList<Station> searchResults, SearchActivity searchActivity) {
        this.searchResults = searchResults;
        this.searchActivity = searchActivity;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_row_item, parent, false);
        itemView.setOnClickListener(searchActivity);
        return new SearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Station station = searchResults.get(position);
        holder.stationText.setText(station.getName());

//        HashMap<String, Boolean> availableTrainLines = station.getTrainLines();
//        for (TrainLine line : TrainLine.values()) {
//            try {
//                if (availableTrainLines.get(line)) {
//                    holder.itemView.findViewWithTag(line).setVisibility(View.VISIBLE);
//                } else {
//                    holder.itemView.findViewWithTag(line).setVisibility(View.GONE);
//                }
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }
}
