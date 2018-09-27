package com.example.dmitry.naviguide.auxiliary;

import com.google.android.gms.maps.model.LatLng;

public class Site {
    public String name, description;
    public double lattitude, longitude;

    public Site(String name, String description, double lattitude, double longitude) {
        this.name = name;
        this.description = description;
        this.lattitude = lattitude;
        this.longitude = longitude;
    }

    public LatLng getLatLng() {
        return new LatLng(lattitude, longitude);
    }
}
