package com.smritiraksha.Patient;

//package com.smritiraksha.Patient;

public class Prescription {
    private String title;
    private String description;
    private int hour;
    private int minute;

    public Prescription(String title, String description, int hour, int minute) {
        this.title = title;
        this.description = description;
        this.hour = hour;
        this.minute = minute;
    }

    public Prescription(int hour, int minute, String title, String description) {
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }
}
