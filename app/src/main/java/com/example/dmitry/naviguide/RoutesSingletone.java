package com.example.dmitry.naviguide;

import android.content.Context;
import android.support.v4.util.Pair;

import com.example.dmitry.naviguide.auxiliary.GeometryHelper;
import com.example.dmitry.naviguide.auxiliary.Site;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoutesSingletone {
    private static RoutesSingletone instance;
    private Context context;

    List<Pair<String, String>> routes;

    private HashMap<String, Site[]> sites;

    public static RoutesSingletone getInstance() {
        synchronized (RoutesSingletone.class) {
            if (instance == null) {
                instance = new RoutesSingletone();
            }
            return instance;
        }
    }

    private RoutesSingletone() {
        routes = new ArrayList<>();
        sites = new HashMap<>();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void loadRoutes(ArrayList<Pair<String, String>> routes) {
        this.routes = routes;
    }

    public void loadSites(HashMap<String, Site[]> sites) {
        for (HashMap.Entry<String, Site[]> entry : sites.entrySet())
            sites.put(entry.getKey(), GeometryHelper.nearestInsertion(entry.getValue()));
        this.sites = sites;
    }

    public List<Pair<String, String>> getRoutes() {
        return routes;
    }

    public HashMap<String, Site[]> getSites() {
        return sites;
    }
}
