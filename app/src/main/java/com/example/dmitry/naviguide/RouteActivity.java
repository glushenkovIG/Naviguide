package com.example.dmitry.naviguide;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RouteActivity extends AppCompatActivity {
    private SectionsPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    String routeName = "";
    MapFragment mapFragment;
    DescrFragment descrFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        routeName = getIntent().getStringExtra("route_name");
        mapFragment = new MapFragment();
        descrFragment = new DescrFragment();
    }

    public static class MapFragment extends Fragment {
        public MapFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.activity_maps, container, false);
        }
    }
    public static class DescrFragment extends Fragment {
        public DescrFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.descr_fragment, container, false);
            ImageView img = (ImageView) view.findViewById(R.id.tour);
            int resourceId = getActivity().getResources().getIdentifier("tour", "drawable", getActivity().getPackageName());
            img.setImageResource(resourceId);
            ((TextView)view.findViewById(R.id.descr_text)).setText(((RouteActivity)getActivity()).routeName);
            ((TextView)view.findViewById(R.id.descr_text)).setShadowLayer(1.6f,1.5f,1.3f, 0);
            return view;
        }

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return position == 1 ? mapFragment : descrFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Описание";
                case 1:
                    return "Карта";
            }
            return null;
        }

    }
}
