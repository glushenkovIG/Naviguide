package com.example.dmitry.naviguide.adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dmitry.naviguide.R;
import com.example.dmitry.naviguide.RouteActivity;
import com.example.dmitry.naviguide.auxiliary.Site;

public class SuperAdapter extends RecyclerView.Adapter<SuperAdapter.ViewHolder> {
    private String routeName;
    private Context context;

    public SuperAdapter(Context context, String route) {
        this.context = context;
        routeName = route;
    }

    @Override
    public SuperAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.super_card_view, parent, false);
        return new SuperAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SuperAdapter.ViewHolder holder, final int position) {
        Site site = ((RouteActivity)context).sites.get(routeName)[position];
        holder.textView.setText(site.name);
        holder.textView.setTypeface(Typeface.createFromAsset(
                context.getAssets(), "font/lobster.otf"));

        holder.descr.setText(site.description);
    }

    @Override
    public int getItemCount() {
        return ((RouteActivity)context).sites.get(routeName).length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener
    {
        private double coef = 3.0;

        private int originalHeight = 0;
        private boolean isViewExpanded = false;
        private Button button;
        private TextView textView, descr;

        public ViewHolder(View v) {
            super(v);

            textView = v.findViewById(R.id.some_text);
            button = v.findViewById(R.id.some_button);
            descr = v.findViewById(R.id.description);

            v.setOnClickListener(this);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Помощь аудиогида", Toast.LENGTH_SHORT).show();
                }
            });

            if (!isViewExpanded) {
                button.setVisibility(View.GONE);
                button.setEnabled(false);

                descr.setVisibility(View.GONE);
            }

        }

        @Override
        public void onClick(final View view) {

            if (originalHeight == 0) {
                originalHeight = view.getHeight();
            }

            // Declare a ValueAnimator object
            ValueAnimator valueAnimator;
            if (!isViewExpanded) {
                button.setVisibility(View.VISIBLE);
                button.setEnabled(true);
                descr.setVisibility(View.VISIBLE);
                isViewExpanded = true;
                valueAnimator = ValueAnimator.ofInt(originalHeight, originalHeight + (int) (originalHeight * coef)); // These values in this method can be changed to expand however much you like
            } else {
                isViewExpanded = false;
                valueAnimator = ValueAnimator.ofInt(originalHeight + (int) (originalHeight * coef), originalHeight);

                Animation a = new AlphaAnimation(1.00f, 0.00f); // Fade out

                a.setDuration((long)(100 * coef));
                // Set a listener to the animation and configure onAnimationEnd
                a.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        button.setVisibility(View.INVISIBLE);
                        button.setEnabled(false);

                        descr.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                // Set the animation on the custom view
                button.startAnimation(a);
                descr.startAnimation(a);
            }
            valueAnimator.setDuration((long)(100 * coef));
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    view.getLayoutParams().height = value.intValue();
                    view.requestLayout();
                }
            });


            valueAnimator.start();

        }


    }
}
