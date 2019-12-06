package com.example.chicagotraintracker;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DrawerAdapter extends ArrayAdapter<String> {

    private String[] drawerItems;
    private static class ViewHolder {
        ImageView drawerIcon;
        TextView drawerText;
    }

    DrawerAdapter(String[] drawerItems, Context context) {
        super(context, R.layout.drawer_list_item, drawerItems);
        this.drawerItems = drawerItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String item = drawerItems[position];
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.drawer_list_item, parent, false);
            viewHolder.drawerIcon = convertView.findViewById(R.id.drawerIcon);
            viewHolder.drawerText = convertView.findViewById(R.id.drawerText);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        switch(item) {
            case ("Nearby Trains"):
                viewHolder.drawerIcon.setImageResource(R.drawable.baseline_location_city_white_36); break;
            case ("CTA Twitter"):
                viewHolder.drawerIcon.setImageResource(R.drawable.twitter_logo); break;
            case ("About"):
                viewHolder.drawerIcon.setImageResource(R.drawable.baseline_info_white_36); break;
        }
        viewHolder.drawerIcon.setColorFilter(Color.WHITE);
        viewHolder.drawerText.setText(item);
        return convertView;
    }
}
