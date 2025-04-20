package com.smritiraksha.CareTaker;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.smritiraksha.Constants;
import com.smritiraksha.R;

import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class tracking_nav extends Fragment implements OnMapReadyCallback {
    private float allowedRadius = 500f; // default in meters
    private LatLng baseLocation = null;

    private GoogleMap mMap;
    private Marker currentMarker;
    private Handler handler = new Handler();
    private RequestQueue requestQueue;
    private TextView locationTextView;
    private Polyline movementPolyline;

    private final List<TimedLocation> movementTrail = new ArrayList<>();
    private static final String LOCATION_URL = Constants.Get_Patient_location;
    private static final int POLL_INTERVAL = 5000; // 5 seconds
    private static final long MAX_DURATION = 24 * 60 * 60 * 1000L; // 24 hours in milliseconds

    private final String patientId = "PT01Sri"; // Replace dynamically later

    public tracking_nav() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracking_nav, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText etRadius = view.findViewById(R.id.et_radius);
        Button btnSetRadius = view.findViewById(R.id.btn_set_radius);

        btnSetRadius.setOnClickListener(v -> {
            if (etRadius.getVisibility() == View.GONE) {
                etRadius.setVisibility(View.VISIBLE);
                btnSetRadius.setText("Save Radius");
            } else {
                String radiusStr = etRadius.getText().toString();
                if (!radiusStr.isEmpty()) {
                    allowedRadius = Float.parseFloat(radiusStr);
                    Toast.makeText(getContext(), "Radius set to " + allowedRadius + " meters", Toast.LENGTH_SHORT).show();
                }
                etRadius.setVisibility(View.GONE);
                btnSetRadius.setText("Set Radius");
            }
        });


        locationTextView = new TextView(requireContext());
        locationTextView.setText("Fetching location...");
        locationTextView.setTextSize(16);
        locationTextView.setPadding(20, 50, 20, 20);
        ((ViewGroup) view).addView(locationTextView);

        requestQueue = Volley.newRequestQueue(requireContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        movementPolyline = mMap.addPolyline(new PolylineOptions().width(6f).color(0xFF0077CC)); // Light blue
        startPollingLocation();
    }

    private void startPollingLocation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchLocationFromServer();
                handler.postDelayed(this, POLL_INTERVAL);
            }
        }, 0);
    }

    private void fetchLocationFromServer() {
        JSONObject postData = new JSONObject();
        try {
            postData.put("patient_id", patientId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, LOCATION_URL, postData,
                response -> {
                    Log.d("LocationFetch", "Response: " + response.toString());
                    try {
                        double latitude = response.getDouble("latitude");
                        double longitude = response.getDouble("longitude");

                        updateMapLocation(latitude, longitude);
                        updateMovementTrail(latitude, longitude);
                        fetchAddress(latitude, longitude);

                    } catch (JSONException e) {
                        Log.e("LocationFetch", "JSON error: " + e.getMessage());
                    }
                },
                error -> Log.e("LocationFetch", "Volley error: " + error.toString())
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        requestQueue.add(request);
    }

    private void updateMapLocation(double lat, double lng) {
        LatLng newLocation = new LatLng(lat, lng);

        // Set base location only once
        if (baseLocation == null) {
            baseLocation = newLocation;
        }

        if (currentMarker == null) {
            currentMarker = mMap.addMarker(new MarkerOptions().position(newLocation).title("Patient Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 20));
        } else {
            currentMarker.setPosition(newLocation);
        }

        // 🚨 Check distance from base
        float[] result = new float[1];
        android.location.Location.distanceBetween(
                baseLocation.latitude, baseLocation.longitude,
                newLocation.latitude, newLocation.longitude,
                result
        );

        if (result[0] > allowedRadius) {
            triggerEmergencyAlert(); // defined below
        }
    }

    private void triggerEmergencyAlert() {
        Vibrator vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(1000); // fallback for older devices
            }
        }

        Toast.makeText(getContext(), "🚨 Patient moved outside safe zone!", Toast.LENGTH_LONG).show();

        // You can also launch a notification or start an emergency service here
    }

    private void updateMovementTrail(double lat, double lng) {
        long currentTime = System.currentTimeMillis();
        movementTrail.add(new TimedLocation(new LatLng(lat, lng), currentTime));

        // Clean up older than 24 hours
        Iterator<TimedLocation> iterator = movementTrail.iterator();
        while (iterator.hasNext()) {
            TimedLocation tl = iterator.next();
            if (currentTime - tl.timestamp > MAX_DURATION) {
                iterator.remove();
            }
        }

        // Draw polyline
        List<LatLng> points = new ArrayList<>();
        for (TimedLocation tl : movementTrail) {
            points.add(tl.latLng);
        }

        if (movementPolyline != null) {
            movementPolyline.setPoints(points);
        }
    }

    private void fetchAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(requireContext());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                String fullAddress = address.getAddressLine(0);
                String floor = address.getSubThoroughfare();

                String displayAddress = "📍 Location:\n" + fullAddress;
                if (floor != null) {
                    displayAddress += "\nFloor Info: " + floor;
                }

                locationTextView.setText(displayAddress);
            } else {
                locationTextView.setText("Address not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
            locationTextView.setText("Failed to fetch address");
        }
    }

    // Helper class to store time with location
    private static class TimedLocation {
        LatLng latLng;
        long timestamp;

        TimedLocation(LatLng latLng, long timestamp) {
            this.latLng = latLng;
            this.timestamp = timestamp;
        }
    }
}
