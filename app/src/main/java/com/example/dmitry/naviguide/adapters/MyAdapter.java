package com.example.dmitry.naviguide.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dmitry.naviguide.R;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private String[] routsNames;
    private Context context;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        Button changeBtn;

        ViewHolder(View v) {
            super(v);
            changeBtn = v.findViewById(R.id.chngBtn);
            textView = v.findViewById(R.id.textView);
        }
    }

    public MyAdapter(Context context) {
        int i = 0;
        this.context = context;
        routsNames = new String[]{"Древняя Москва",  "Культурная Москва", "Шоппинг в Москве"};
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

        holder.changeBtn.setOnClickListener(new ChangeBtnListener(position));
    }


    private class ChangeBtnListener implements View.OnClickListener {

        private int position;
        ChangeBtnListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View view) {
//            if (context instanceof MainActivity)
//                ((MainActivity) context).startCurrencyMenuActivity(mCurrencyNamesSet[position]);
//            else if (context instanceof CurrencyMenuActivity)
//                ((CurrencyMenuActivity) context)
//                        .showComparisionWithAnotherCurrency(mCurrencyNamesSet[position],
//                                ((CurrencyMenuActivity)context).getLastDate());
        }
    }

    @Override
    public int getItemCount() {
        return routsNames.length;
    }
}