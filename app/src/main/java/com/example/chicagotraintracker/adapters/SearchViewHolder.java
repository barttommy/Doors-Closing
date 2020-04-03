package com.example.chicagotraintracker.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chicagotraintracker.R;

public class SearchViewHolder extends RecyclerView.ViewHolder {

    TextView stationText;

    public SearchViewHolder(@NonNull View itemView) {
        super(itemView);

        // TODO
        stationText = itemView.findViewById(R.id.search_station);
    }
}
