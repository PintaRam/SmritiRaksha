package com.smritiraksha.Doctor;

import java.io.Serializable;

public class Prescription implements Serializable {
    private String id;
    private String patientId;
    private String title;
    private String description;
    private String hour;
    private String minute;
    private String createdAt;

    public Prescription(String id, String patientId, String title, String description, String hour, String minute, String createdAt) {
        this.id = id;
        this.patientId = patientId;
        this.title = title;
        this.description = description;
        this.hour = hour;
        this.minute = minute;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
