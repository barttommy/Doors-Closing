package com.tommybart.chicagotraintracker.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tommybart.chicagotraintracker.R;
import com.tommybart.chicagotraintracker.data.models.Route;

class SearchViewHolder extends RecyclerView.ViewHolder {

    TextView stationText;

    SearchViewHolder(@NonNull View itemView) {
        super(itemView);

        stationText = itemView.findViewById(R.id.search_station);

        itemView.findViewById(R.id.search_red_line)
                .setTag(Route.RED_LINE);
        itemView.findViewById(R.id.search_blue_line)
                .setTag(Route.BLUE_LINE);
        itemView.findViewById(R.id.search_green_line)
                .setTag(Route.GREEN_LINE);
        itemView.findViewById(R.id.search_brown_line)
                .setTag(Route.BROWN_LINE);
        itemView.findViewById(R.id.search_purple_line)
                .setTag(Route.PURPLE_LINE);
        itemView.findViewById(R.id.search_yellow_line)
                .setTag(Route.YELLOW_LINE);
        itemView.findViewById(R.id.search_pink_line)
                .setTag(Route.PINK_LINE);
        itemView.findViewById(R.id.search_orange_line)
                .setTag(Route.ORANGE_LINE);
    }
}
