package com.example.chicagotraintracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chicagotraintracker.R;
import com.example.chicagotraintracker.activities.MainActivity;
import com.example.chicagotraintracker.activities.SearchActivity;
import com.example.chicagotraintracker.models.Station;

import java.util.ArrayList;

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
        Station selection = searchResults.get(position);

        // TODO
        holder.stationText.setText(selection.getDetailedName());
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }
}
