package com.smritiraksha.Models;

public class MedItem {
    private int id;
    private String email;

    public MedItem(int x, String y) {
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
