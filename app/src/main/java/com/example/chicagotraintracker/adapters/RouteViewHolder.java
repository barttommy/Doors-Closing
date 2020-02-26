package com.example.chicagotraintracker.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chicagotraintracker.R;

class RouteViewHolder extends RecyclerView.ViewHolder {

    TextView stationText;
    TextView directionText;
    TextView arrivalsText;
    TextView timeText;
    ImageView trainImage;

    RouteViewHolder(@NonNull View itemView) {
        super(itemView);
        stationText = itemView.findViewById(R.id.stationTextView);
        directionText = itemView.findViewById(R.id.directionTextView);
        arrivalsText = itemView.findViewById(R.id.arrivalsTextView);
        timeText = itemView.findViewById(R.id.timeTextView);
        trainImage = itemView.findViewById(R.id.trainImageView);
    }
}
