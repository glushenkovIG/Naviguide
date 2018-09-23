package com.example.dmitry.naviguide.auxiliary;

public class Site {
    public String name, description;
    public double lattitude, longitude;

    public Site(String name, String description, double lattitude, double longitude) {
        this.name = name;
        this.description = description;
        this.lattitude = lattitude;
        this.longitude = longitude;
    }
}
