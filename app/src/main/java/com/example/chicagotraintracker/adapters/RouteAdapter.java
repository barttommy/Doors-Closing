package com.example.chicagotraintracker.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chicagotraintracker.R;
import com.example.chicagotraintracker.models.Route;
import com.example.chicagotraintracker.models.Train;
import com.example.chicagotraintracker.activities.MainActivity;

import java.util.ArrayList;

public class RouteAdapter extends RecyclerView.Adapter<RouteViewHolder> {

    private ArrayList<Route> routeList;
    private MainActivity mainActivity;

    public RouteAdapter(ArrayList<Route> routeList, MainActivity mainActivity) {
        this.routeList = routeList;
        this.mainActivity = mainActivity;
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
        switch(line) {
            case (Route.BROWN_LINE):
                return mainActivity.getResources().getColor(R.color.brownLine, null);
            case (Route.PURPLE_LINE):
                return mainActivity.getResources().getColor(R.color.purpleLine, null);
            case (Route.RED_LINE):
                return mainActivity.getResources().getColor(R.color.redLine, null);
            case (Route.BLUE_LINE):
                return mainActivity.getResources().getColor(R.color.blueLine, null);
            case (Route.GREEN_LINE):
                return mainActivity.getResources().getColor(R.color.greenLine, null);
            case (Route.ORANGE_LINE):
                return mainActivity.getResources().getColor(R.color.orangeLine, null);
            case (Route.PINK_LINE):
                return mainActivity.getResources().getColor(R.color.pinkLine, null);
            case (Route.YELLOW_LINE):
                return mainActivity.getResources().getColor(R.color.yellowLine, null);
            default:
                return Color.WHITE;
        }
    }
}
