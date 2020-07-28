package com.tommybart.chicagotraintracker.adapters;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tommybart.chicagotraintracker.R;
import com.tommybart.chicagotraintracker.data.models.Route;
import com.tommybart.chicagotraintracker.data.models.Train;
import com.tommybart.chicagotraintracker.activities.ArrivalsActivity;

import java.util.ArrayList;

public class RouteAdapter extends RecyclerView.Adapter<RouteViewHolder> {

    private ArrayList<Route> routeList;
    private ArrivalsActivity arrivalsActivity;

    public RouteAdapter(ArrayList<Route> routeList, ArrivalsActivity arrivalsActivity) {
        this.routeList = routeList;
        this.arrivalsActivity = arrivalsActivity;
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

        String line = selection.getLine();
        int color = getColor(line);
        holder.trainImage.setColorFilter(color);
        holder.directionText.setTextColor(color);
        holder.stationText.setText(selection.getStationName());
        holder.directionText.setText(selection.getDestination());
        holder.arrivalsText.setText("");
        holder.timeText.setText("");

        ArrayList<Train> trains = selection.getTrains();
        int size = trains.size();
        for (int i = 0; i < size; i++) {
            if (i == (size - 1)) {
                holder.arrivalsText.append(trains.get(i).getArrivalTime());
                holder.timeText.append(trains.get(i).getTimeRemaining());
            } else {
                holder.arrivalsText.append(trains.get(i).getArrivalTime() + "\n");
                holder.timeText.append(trains.get(i).getTimeRemaining() + "\n");
            }
        }
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    private int getColor(String line) {
        Resources res = arrivalsActivity.getResources();
        switch(line) {
            case (Route.BROWN_LINE):
                return res.getColor(R.color.brownLine, null);
            case (Route.PURPLE_LINE):
                return res.getColor(R.color.purpleLine, null);
            case (Route.RED_LINE):
                return res.getColor(R.color.redLine, null);
            case (Route.BLUE_LINE):
                return res.getColor(R.color.blueLine, null);
            case (Route.GREEN_LINE):
                return res.getColor(R.color.greenLine, null);
            case (Route.ORANGE_LINE):
                return res.getColor(R.color.orangeLine, null);
            case (Route.PINK_LINE):
                return res.getColor(R.color.pinkLine, null);
            case (Route.YELLOW_LINE):
                return res.getColor(R.color.yellowLine, null);
            default:
                return Color.WHITE;
        }
    }
}
