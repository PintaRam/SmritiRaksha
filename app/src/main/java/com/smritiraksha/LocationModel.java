package com.smritiraksha;

public class LocationModel {
    private int id;
    private String location_name;
    private String visit_time;

    public LocationModel(int id, String location_name, String visit_time) {
        this.id = id;
        this.location_name = location_name;
        this.visit_time = visit_time;
    }

    public int getId() {
        return id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public String getVisit_time() {
        return visit_time;
    }
}
