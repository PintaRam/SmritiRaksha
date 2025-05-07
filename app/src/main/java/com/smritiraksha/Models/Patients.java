package com.smritiraksha.Models;
public class Patients {

    private String id;
    private String name;


    public Patients(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
