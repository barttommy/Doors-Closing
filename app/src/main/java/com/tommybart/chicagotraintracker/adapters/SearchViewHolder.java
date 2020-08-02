package com.tommybart.chicagotraintracker.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tommybart.chicagotraintracker.R;
import com.tommybart.chicagotraintracker.data.models.Route;
import com.tommybart.chicagotraintracker.internal.TrainLine;

class SearchViewHolder extends RecyclerView.ViewHolder {

    TextView stationText;

    SearchViewHolder(@NonNull View itemView) {
        super(itemView);

        stationText = itemView.findViewById(R.id.search_station);

        itemView.findViewById(R.id.search_red_line)
                .setTag(TrainLine.RED);
        itemView.findViewById(R.id.search_blue_line)
                .setTag(TrainLine.BLUE);
        itemView.findViewById(R.id.search_green_line)
                .setTag(TrainLine.GREEN);
        itemView.findViewById(R.id.search_brown_line)
                .setTag(TrainLine.BROWN);
        itemView.findViewById(R.id.search_purple_line)
                .setTag(TrainLine.PURPLE);
        itemView.findViewById(R.id.search_yellow_line)
                .setTag(TrainLine.YELLOW);
        itemView.findViewById(R.id.search_pink_line)
                .setTag(TrainLine.PINK);
        itemView.findViewById(R.id.search_orange_line)
                .setTag(TrainLine.ORANGE);
    }
}
