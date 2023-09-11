package com.testmongo.demo.model;

import lombok.Data;

@Data
public class Location implements Comparable<Location> {

    private String name;

    private String lat;

    private String lon;

    private double distance;

    public Location(String nm, String lt, String ln, double dt) {

        name = nm;
        lat = lt;
        lon = ln;
        distance = dt;

    }

    public void print() {
        System.out.printf("%10s %10s %10s %10f\n", name, lat, lon, distance);
    }

    @Override
    public int compareTo(Location other) {

        if (this.distance > other.distance) return 1;
        else if (this.distance < other.distance) return -1;
        return 0;
    }
}
