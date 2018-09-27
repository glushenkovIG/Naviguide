package com.example.dmitry.naviguide;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.example.dmitry.naviguide.adapters.MyAdapter;
import com.example.dmitry.naviguide.auxiliary.CustomRecyclerView;

public class RoutesListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routs_list_activity);
        recyclerViewSet();
    }

    protected void recyclerViewSet() {
        CustomRecyclerView recycleView = (CustomRecyclerView) findViewById(R.id.recycler);
        recycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycleView.setAdapter(new MyAdapter(this));
    }

    private PendingIntent getActivityPendingIntent() {
        Intent activityIntent = new Intent(this, RoutesListActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerViewSet();
    }

    public void callRouteActivity(String name) {
        Intent intent = new Intent(RoutesListActivity.this, RouteActivity.class);
        intent.putExtra("route_name", name);
        startActivity(intent);
    }
}
