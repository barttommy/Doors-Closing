package com.tommybart.chicagotraintracker.adapters;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MarginItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public MarginItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = space;
        }
        outRect.bottom = space;
        outRect.left = space;
        outRect.right = space;
    }
}
