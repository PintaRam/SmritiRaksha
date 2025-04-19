package com.smritiraksha.CareTaker;

import com.google.android.gms.maps.model.LatLng;

public class TimedLocation {
    public LatLng latLng;
    public long timestamp; // time in milliseconds

    public TimedLocation(LatLng latLng, long timestamp) {
        this.latLng = latLng;
        this.timestamp = timestamp;
    }
}
