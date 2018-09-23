package com.example.dmitry.naviguide.auxiliary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class SuperRecyclerView extends RecyclerView {
    Context context;

    public SuperRecyclerView(Context context) {
        super(context);
        this.context = context;
    }

    public SuperRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SuperRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityY *= 0.4;
        return super.fling(velocityX, velocityY);
    }
}
