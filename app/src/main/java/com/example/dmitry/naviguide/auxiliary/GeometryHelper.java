package com.example.dmitry.naviguide.auxiliary;

import java.util.ArrayList;

public class GeometryHelper {

    //h1-h2=0
    public static double distance(Site s1, Site s2) {
        final double R = 6371;
        double latDistance = Math.toRadians(s1.lattitude - s2.lattitude);
        double lonDistance = Math.toRadians(s1.longitude - s2.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(s1.lattitude)) * Math.cos(Math.toRadians(s2.lattitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000;
    }

    public static Site[] nearestInsertion(Site[] sites) {
        if (sites.length < 2)
            return sites;
        ArrayList<Site> ans = new ArrayList<>(sites.length);
        double cur_min = 0;
        int best = -1, best1 = -1;
        for (int i = 0; i < sites.length; ++i)
            for (int j = i + 1; j < sites.length; ++j) {
                if ((best == -1 && best1 == -1) || distance(sites[i], sites[j]) < cur_min) {
                    cur_min = distance(sites[i], sites[j]);
                    best = i;
                    best1 = j;
                }
                if (i == sites.length - 2 && j == sites.length - 1) {
                    ans.add(sites[best]);
                    ans.add(sites[best1]);
                    sites[best] = null;
                    sites[best1] = null;
                }
            }

        for (int q = 0; q < sites.length - 2; ++q) {
            best = -1;
            for (int i = 0; i < 2 + q; ++i)
                for (int j = 0; j < sites.length; ++j) {
                    if (sites[j] == null)
                        continue;
                    if (best == -1 || distance(ans.get(i), sites[j]) < cur_min) {
                        cur_min = distance(ans.get(i), sites[j]);
                        best = j;
                    }
                }
            best1 = -1;

            for (int i = 0; i < ans.size(); ++i) {
                double d = distance(ans.get(i), ans.get((i + 1) % ans.size())) +
                        distance(ans.get(i), sites[best]) +
                        distance(ans.get((i + 1) % ans.size()), sites[best]);
                if (best1 == -1 || d < cur_min) {
                    cur_min = d;
                    best1 = i;
                }
            }

            ans.add(best1, sites[best]);
            sites[best] = null;
        }

        return ans.toArray(new Site[sites.length]);
    }
}
