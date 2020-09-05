package com.tommybart.chicagotraintracker.ui.activities.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tommybart.chicagotraintracker.R;
import com.tommybart.chicagotraintracker.data.models.Station;
import com.tommybart.chicagotraintracker.internal.TrainLine;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map.Entry;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder> {

    private ArrayList<Station> searchResults;
    private LayoutInflater layoutInflater;
    private OnClickListener onClickListener;

    SearchRecyclerAdapter(Context context, ArrayList<Station> searchResults) {
        layoutInflater = LayoutInflater.from(context);
        this.searchResults = searchResults;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.row_search, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Station station = searchResults.get(position);
        holder.stationText.setText(station.getName());

        EnumMap<TrainLine, Boolean> availableTrainLines = station
                .getAvailableTrainLines()
                .getValues();
        Boolean purple = false;
        for (Entry<TrainLine, Boolean> entry : availableTrainLines.entrySet()) {
            if (entry.getKey() == TrainLine.PURPLE || entry.getKey() == TrainLine.PURPLE_EXPRESS) {
                purple = purple || entry.getValue();
                setTrainLineVisibility(holder, TrainLine.PURPLE, purple);
            } else {
                setTrainLineVisibility(holder, entry.getKey(), entry.getValue());
            }
        }
    }

    private void setTrainLineVisibility(@NonNull ViewHolder holder, TrainLine trainLine,
                                        Boolean isAvailable) {
        if (isAvailable) {
            holder.itemView.findViewWithTag(trainLine).setVisibility(View.VISIBLE);
        } else {
            holder.itemView.findViewWithTag(trainLine).setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView stationText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            stationText = itemView.findViewById(R.id.row_search_tv_station);

            itemView.findViewById(R.id.row_search_iv_red).setTag(TrainLine.RED);
            itemView.findViewById(R.id.row_search_iv_blue).setTag(TrainLine.BLUE);
            itemView.findViewById(R.id.row_search_iv_brown).setTag(TrainLine.BROWN);
            itemView.findViewById(R.id.row_search_iv_green).setTag(TrainLine.GREEN);
            itemView.findViewById(R.id.row_search_iv_orange).setTag(TrainLine.ORANGE);
            itemView.findViewById(R.id.row_search_iv_pink).setTag(TrainLine.PINK);
            itemView.findViewById(R.id.row_search_iv_purple).setTag(TrainLine.PURPLE);
            itemView.findViewById(R.id.row_search_iv_yellow).setTag(TrainLine.YELLOW);
        }

        @Override
        public void onClick(View view) {
            if (onClickListener != null) onClickListener.onClick(view);
        }
    }
}
