package com.smritiraksha.Models;

public class MonitorItem {
    private int id;
    private String email;

    public MonitorItem(int x, String y) {
        this.id = x;
        this.email = y;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
