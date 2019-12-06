package com.example.chicagotraintracker;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RouteAdapter extends RecyclerView.Adapter<RouteViewHolder> {

    private ArrayList<Route> routeList;
    private MainActivity mainActivity;

    RouteAdapter(ArrayList<Route> routeList, MainActivity mainActivity) {
        this.routeList = routeList;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item, parent, false);
        itemView.setOnClickListener(mainActivity);
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
        switch(line) {
            case ("Brn"):
                return mainActivity.getResources().getColor(R.color.brownLine, null);
            case ("P"):
                return mainActivity.getResources().getColor(R.color.purpleLine, null);
            case ("Red"):
                return mainActivity.getResources().getColor(R.color.redLine, null);
            case ("Blue"):
                return mainActivity.getResources().getColor(R.color.blueLine, null);
            case ("G"):
                return mainActivity.getResources().getColor(R.color.greenLine, null);
            case ("Org"):
                return mainActivity.getResources().getColor(R.color.orangeLine, null);
            case ("Pink"):
                return mainActivity.getResources().getColor(R.color.pinkLine, null);
            case ("Y"):
                return mainActivity.getResources().getColor(R.color.yellowLine, null);
            default:
                return Color.WHITE;
        }
    }
}
