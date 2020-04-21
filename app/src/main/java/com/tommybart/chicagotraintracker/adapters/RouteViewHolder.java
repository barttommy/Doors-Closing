package com.tommybart.chicagotraintracker.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tommybart.chicagotraintracker.R;

class RouteViewHolder extends RecyclerView.ViewHolder {

    TextView stationText;
    TextView directionText;
    TextView arrivalsText;
    TextView timeText;
    ImageView trainImage;

    RouteViewHolder(@NonNull View itemView) {
        super(itemView);
        stationText = itemView.findViewById(R.id.route_station_text);
        directionText = itemView.findViewById(R.id.route_direction_text);
        arrivalsText = itemView.findViewById(R.id.route_arrivals_text);
        timeText = itemView.findViewById(R.id.route_time_text);
        trainImage = itemView.findViewById(R.id.route_train_image);
    }
}
