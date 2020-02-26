package com.example.chicagotraintracker.adapters;

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

import com.example.chicagotraintracker.R;

public class DrawerAdapter extends ArrayAdapter<String> {

    private String[] drawerItems;
    private static class ViewHolder {
        ImageView drawerIcon;
        TextView drawerText;
    }

    public DrawerAdapter(String[] drawerItems, Context context) {
        super(context, R.layout.drawer_list_item, drawerItems);
        this.drawerItems = drawerItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
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

        String item = drawerItems[position];
        if (item.equals(drawerItems[0])) {
            viewHolder.drawerIcon.setImageResource(R.drawable.baseline_location_city_white_36);
        } else if (item.equals(drawerItems[1])) {
            viewHolder.drawerIcon.setImageResource(R.drawable.twitter_logo);
        } else if (item.equals(drawerItems[2])) {
            viewHolder.drawerIcon.setImageResource(R.drawable.baseline_info_white_36);
        }

        viewHolder.drawerIcon.setColorFilter(Color.WHITE);
        viewHolder.drawerText.setText(item);
        return convertView;
    }
}
