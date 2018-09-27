package com.example.dmitry.naviguide;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dmitry.naviguide.auxiliary.Site;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    HashMap<Pair<String, LatLng>, ArrayList<Pair<String, Integer>>> suggestions = new HashMap<>();
    private GoogleMap mMap;

    public DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        dbHelper = new DBHelper(getApplicationContext());

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT site.name, route_id, route.name, site.lat, site.lng FROM site, route  WHERE route.rowid=site.route_id ORDER BY RANDOM() LIMIT 10;", null);
        cursor.moveToFirst();

        do {
            Pair<String, LatLng> site = new Pair<>(cursor.getString(0), new LatLng(cursor.getDouble(3), cursor.getDouble(4)));
            ArrayList<Pair<String, Integer>> routes = suggestions.get(site);
            if (routes == null) {
                routes = new ArrayList<>();
                routes.add(new Pair<>(cursor.getString(2), cursor.getInt(1)));
            }
            suggestions.put(site, routes);
        } while (cursor.moveToNext());


        (findViewById(R.id.demo_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT rowid, name, picture FROM route", null);
                cursor.moveToFirst();
                ArrayList<Pair<String, String>> routes = new ArrayList<>();
                HashMap<String, Site[]> sites = new HashMap<>();
                do {
                    routes.add(new Pair<>(cursor.getString(1), cursor.getString(2)));
                    Cursor cursor1 = db.rawQuery("SELECT name, descr, lat, lng FROM site WHERE route_id = " + String.valueOf(cursor.getInt(0)), null);
                    cursor1.moveToFirst();
                    ArrayList<Site> sites1 = new ArrayList<>();
                    do {
                        sites1.add(new Site(cursor1.getString(0), cursor1.getString(1), cursor1.getDouble(2), cursor1.getDouble(3)));
                    } while (cursor1.moveToNext());
                    cursor1.close();
                    sites.put(routes.get(routes.size() - 1).first, sites1.toArray(new Site[sites1.size()]));
                } while (cursor.moveToNext());
                cursor.close();

                RoutesSingletone.getInstance().loadRoutes(routes);
                RoutesSingletone.getInstance().loadSites(sites);

                callMainActivity();
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override public void onInfoWindowClick(Marker marker) {
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    ArrayList<Pair<String, String>> routes = new ArrayList<>();
                    HashMap<String, Site[]> sites = new HashMap<>();
                    for (Pair<String, Integer> pair : suggestions.get(new Pair<>(marker.getTitle(), marker.getPosition()))) {
                        int id = pair.second;
                        Cursor cursor = db.rawQuery("SELECT rowid, name, picture FROM route WHERE rowid = " + String.valueOf(id) +";", null);
                        cursor.moveToFirst();
                        do {
                            routes.add(new Pair<>(cursor.getString(1), cursor.getString(2)));
                            Cursor cursor1 = db.rawQuery("SELECT name, descr, lat, lng FROM site WHERE route_id = " + String.valueOf(cursor.getInt(0)), null);
                            cursor1.moveToFirst();
                            ArrayList<Site> sites1 = new ArrayList<>();
                            do {
                                sites1.add(new Site(cursor1.getString(0), cursor1.getString(1), cursor1.getDouble(2), cursor1.getDouble(3)));
                            } while (cursor1.moveToNext());
                            cursor1.close();
                            sites.put(routes.get(routes.size() - 1).first, sites1.toArray(new Site[sites1.size()]));
                        } while (cursor.moveToNext());
                        cursor.close();
                    }

                    RoutesSingletone.getInstance().loadRoutes(routes);
                    RoutesSingletone.getInstance().loadSites(sites);

                    callMainActivity();
            }
        });


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

            for (Pair<String, LatLng> site : suggestions.keySet())
                setMarker(site.first, "", site.second);

//            setMarker("Улица Старый Арбат",
//                    "Cтapый Apбaт — oднa из глaвныx дocтoпpимeчaтeльнocтeй Mocквы, oчeнь пoпyляpнaя cpeди тypиcтoв пeшexoднaя yлицa большим количеством cyвeниpныx лaвoк, кафе и магазинов. На Арбате находится ряд достопримечательностей такие как театр имени Вахтангова, дома известных писателей и поэтов (Пушкина, Андрея Белого) и стена памяти рок-музыканта Виктора Цоя, к которой ежегодно приходят тысячи любителей его творчества дабы почтить память. Магазины здесь на любой вкус - от современных бутиков до кондитерских лавок советского периода, где можно попробовать старые добрые десерты такие как “Птичье молоко”."
//                    , new LatLng(55.7496713, 37.5922520));
//            setMarker("Большой николопесковский переулок",
//                    "театр Вахтангова",
//                    new LatLng(55.7496091, 37.5911241));
//            setMarker("Apartment on Bolshoi Nikolopeskovskyi pereulok 3",
//                    "Данный адрес больше не обслуживается booking.com",
//                    new LatLng(55.7500970, 37.5893784));
//
//            setMarker("Studio Old Arbat",
//                    "Апартаменты-студио Old Arbat с гостиной зоной, телевизором с плоским экраном и бесплатным Wi-Fi расположены в Москве, в 200 м от улицы Арбат",
//                    new LatLng(55.7492795, 37.5899785));
//
//            setMarker("Apartments on Arbat Street",
//                    "Апартаменты «На Арбате» с собственной кухней расположены в центре Москвы. Историческая улица Старый Арбат находится всего в 400 метрах, а музей изобразительных искусств имени А.С. Пушкина — в 1 км. К услугам гостей бесплатный WiFi.",
//                    new LatLng(55.7490940, 37.5901258));
//
//            setMarker("Landmark City Hostel",
//                    "Хостел Landmark City расположен на оживленной улице Арбат в историческом центре Москвы. К услугам гостей частный сад, библиотека, лаундж и бесплатный Wi-Fi. В номерах есть место для работы. Гости пользуются общей ванной комнатой с феном.",
//                    new LatLng(55.7485560, 37.5892050));
//
//            setMarker("Apartment on Arbat 31",
//                    "Апартаменты «На Арбате 31» расположены в Москве, в нескольких шагах от улицы Арбат, в 800 м от Музея изобразительных искусств имени А. С. Пушкина и в 12 минутах ходьбы от храма Христа Спасителя.",
//                    new LatLng(55.7497888, 37.5931081));
//
//            setMarker("Lux Apartments - Kaloshin pereulok",
//                    "Данный адрес больше не обслуживается booking.com",
//                    new LatLng(55.7483431, 37.5923632));

        }

        private void setMarker(String title, String subTitle, LatLng latLng) {
            MarkerOptions markerOpt = new MarkerOptions();
            markerOpt.title(title).snippet(subTitle);
            markerOpt.position(latLng);
            markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(this);
            mMap.setInfoWindowAdapter(adapter);
            mMap.addMarker(markerOpt);
        }

        protected synchronized void buildGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
        @Override
        public void onConnected(Bundle bundle) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest, this);
            }
        }
        @Override
        public void onConnectionSuspended(int i) {
        }
        @Override
        public void onLocationChanged(Location location) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f));
            /*mLastLocation = location;
    //Showing Current Location Marker on Map
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
                    getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                        this);
            }
            */
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void callMainActivity() {
        Intent intent = new Intent(MapsActivity.this, RoutesListActivity.class);
        startActivity(intent);
        //MapsActivity.this.finish();
    }
}

class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity context;

    public CustomInfoWindowAdapter(Activity context){
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.customwindow, null);

        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvSubTitle = (TextView) view.findViewById(R.id.tv_subtitle);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        tvTitle.setText(marker.getTitle());
        tvSubTitle.setText(marker.getSnippet());


        if (marker.getTitle() != null) {
            if (marker.getTitle().equals("Улица Старый Арбат")) {
                icon.setImageResource(R.mipmap.arbat_background);
            }
        }
        return view;
    }
}