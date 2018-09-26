package com.example.dmitry.naviguide.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dmitry.naviguide.R;
import com.example.dmitry.naviguide.RoutesListActivity;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private String[] routsNames;
    private Context context;
    private ImageView image;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        Button changeBtn;

        ViewHolder(View v) {
            super(v);
            changeBtn = v.findViewById(R.id.chngBtn);
            textView = v.findViewById(R.id.textView);
            image = v.findViewById(R.id.image);
        }
    }

    public MyAdapter(Context context) {
        this.context = context;
        routsNames = new String[]{"Древняя Москва",  "Культурная Москва", "Шоппинг в Москве", "Ночная Москва", "Парки Москвы", "Мраморные пещеры Крыма", "Грязевые вулканы Гобустана", "Лучшие граффити Берлина"};
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.textView.setText(routsNames[position]);
        holder.textView.setTypeface(Typeface.createFromAsset(
                context.getAssets(), "font/lobster.otf"));

        ChangeBtnListener cbl = new ChangeBtnListener(position);
        holder.changeBtn.setOnClickListener(cbl);
        TypedArray imgs = context.getResources().obtainTypedArray(R.array.pictures);
        int resourceId = context.getResources().getIdentifier(imgs.getString(position), "drawable", context.getPackageName());
        image.setImageResource(resourceId);
        image.setOnClickListener(cbl);
        imgs.recycle();
    }


    private class ChangeBtnListener implements View.OnClickListener {

        private int position;
        ChangeBtnListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (context instanceof RoutesListActivity)
                ((RoutesListActivity) context).callRouteActivity(routsNames[position]);
        }
    }

    @Override
    public int getItemCount() {
        return routsNames.length;
    }
}