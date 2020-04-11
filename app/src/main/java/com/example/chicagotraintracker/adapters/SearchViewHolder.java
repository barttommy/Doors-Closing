package com.example.chicagotraintracker.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chicagotraintracker.R;

class SearchViewHolder extends RecyclerView.ViewHolder {

    TextView stationText;
    ImageView redLine, blueLine, greenLine, brownLine, purpleLine,
            yellowLine, pinkLine, orangeLine;

    SearchViewHolder(@NonNull View itemView) {
        super(itemView);

        stationText = itemView.findViewById(R.id.search_station);
        redLine = itemView.findViewById(R.id.search_red_line);
        blueLine = itemView.findViewById(R.id.search_blue_line);
        greenLine = itemView.findViewById(R.id.search_green_line);
        brownLine = itemView.findViewById(R.id.search_brown_line);
        purpleLine = itemView.findViewById(R.id.search_purple_line);
        yellowLine = itemView.findViewById(R.id.search_yellow_line);
        pinkLine = itemView.findViewById(R.id.search_pink_line);
        orangeLine = itemView.findViewById(R.id.search_orange_line);
    }
}
