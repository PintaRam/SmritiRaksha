package com.smritiraksha.CareTaker;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.smritiraksha.Constants;
import com.smritiraksha.R;
import android.location.Address;
import android.location.Geocoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class tracking_nav extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker currentMarker;
    private Handler handler = new Handler();
    private RequestQueue requestQueue;
    private TextView locationTextView;

    private static final String LOCATION_URL = Constants.Get_Patient_location; // Update this
    private static final int POLL_INTERVAL = 5000; // 5 seconds

    public tracking_nav() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracking_nav, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

    private String patientId = "PT01Sri"; // You should set this dynamically based on login/session

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
                        fetchAddress(latitude, longitude); // new method

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
        if (currentMarker == null) {
            currentMarker = mMap.addMarker(new MarkerOptions().position(newLocation).title("Patient Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 20));
        } else {
            currentMarker.setPosition(newLocation);
        }
    }
    private void fetchAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(requireContext());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                String fullAddress = address.getAddressLine(0); // full address
                String floor = address.getSubThoroughfare(); // can include building info sometimes

                // You can customize this display text
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

}
