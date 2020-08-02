package com.tommybart.chicagotraintracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tommybart.chicagotraintracker.R;
import com.tommybart.chicagotraintracker.activities.ArrivalsActivity;
import com.tommybart.chicagotraintracker.data.models.Route;
import com.tommybart.chicagotraintracker.data.models.Train;

import java.util.ArrayList;

public class RouteAdapter extends RecyclerView.Adapter<RouteViewHolder> {

    private ArrayList<Route> routeList;
    private Context context;

    public RouteAdapter(ArrayList<Route> routeList, Context context) {
        this.routeList = routeList;
        this.context = context;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.route_row_item, parent, false);
        return new RouteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {

        Route selection = routeList.get(position);
        int color = selection.getTrainLine().getColor(context.getResources());

        holder.trainImage.setColorFilter(color);
        holder.directionText.setTextColor(color);
        holder.stationText.setText(selection.getStationName());
        holder.directionText.setText(selection.getDestinationName());
        holder.arrivalsText.setText("");
        holder.timeText.setText("");

        ArrayList<Train> trains = selection.getArrivals();
        int size = trains.size();
        for (int i = 0; i < size; i++) {
            int minutes = trains.get(i).getArrivalTimeMinutes();
            String timeRemaining = (minutes <= 1) ? "Due" : minutes + " min";
            if (i == (size - 1)) {
                holder.arrivalsText.append(trains.get(i).getArrivalTimeDetail());
                holder.timeText.append(timeRemaining);
            } else {
                holder.arrivalsText.append(trains.get(i).getArrivalTimeDetail() + "\n");
                holder.timeText.append(timeRemaining + "\n");
            }
        }
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }
}
