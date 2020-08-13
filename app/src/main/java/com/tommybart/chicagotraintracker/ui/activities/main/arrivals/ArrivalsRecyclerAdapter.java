package com.tommybart.chicagotraintracker.ui.activities.main.arrivals;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tommybart.chicagotraintracker.R;
import com.tommybart.chicagotraintracker.data.models.Route;
import com.tommybart.chicagotraintracker.data.models.Train;

import java.util.ArrayList;

// TODO clean up?
public class ArrivalsRecyclerAdapter
        extends RecyclerView.Adapter<ArrivalsRecyclerAdapter.ViewHolder> {

    private ArrayList<Route> routeList;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnClickListener onClickListener;

    public ArrivalsRecyclerAdapter(Context context, ArrayList<Route> routeList) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.routeList = routeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.row_arrivals, parent, false);
        return new ArrivalsRecyclerAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

    void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView stationText;
        TextView directionText;
        TextView arrivalsText;
        TextView timeText;
        ImageView trainImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            stationText = itemView.findViewById(R.id.arrivals_row_tv_station);
            directionText = itemView.findViewById(R.id.arrivals_row_tv_direction);
            arrivalsText = itemView.findViewById(R.id.route_arrivals_text);
            timeText = itemView.findViewById(R.id.arrivals_row_tv_time);
            trainImage = itemView.findViewById(R.id.arrivals_row_iv_train);
        }

        @Override
        public void onClick(View view) {
            if (onClickListener != null) onClickListener.onClick(view);
        }
    }
}
