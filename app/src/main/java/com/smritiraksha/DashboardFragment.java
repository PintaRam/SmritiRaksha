package com.smritiraksha;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment implements SensorEventListener {
    private FusedLocationProviderClient fusedLocationClient;
    private SensorManager sensorManager;
    private Sensor heartRateSensor;
    private Sensor stepCounterSensor;
    private TextView currentLocationTextView;
    private TextView heartRateTextView;
    private TextView stepsTextView;
    private Button wordSearchButton;

    private int stepsWalked = 0;
    private float currentHeartRate = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return  inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Initialize SensorManager and get the Heart Rate and Step Counter sensors
        sensorManager = (SensorManager) requireContext().getSystemService(getContext().SENSOR_SERVICE);
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Link TextViews
        currentLocationTextView = view.findViewById(R.id.current_location_text);
        heartRateTextView = view.findViewById(R.id.heart_rate_text);
        stepsTextView = view.findViewById(R.id.steps_text);
        wordSearchButton=view.findViewById(R.id.word_search_play_button);

        wordSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent wrdsrch= new Intent(requireContext(), Word_Search_Game.class);
                startActivity(wrdsrch);
            }
        });

        // Fetch Current Location
        fetchCurrentLocation();

        // Register sensor listeners for heart rate and steps
        if (heartRateSensor != null) {
            sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_UI);
        }
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
        // Add OnClickListener for Logic Play button
        Button logicPlayButton = view.findViewById(R.id.logic_puzzle_play_button);
        logicPlayButton.setOnClickListener(v -> {
            // Start LogicPuzzleSplash Activity
            Intent intent = new Intent(requireContext(), LogicPuzzleSplash.class);
            startActivity(intent);
        });
    }

    private void fetchCurrentLocation() {
        // Check Permissions
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request Permissions
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        // Get Last Known Location
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {
                            // Convert latitude and longitude to address
                            getAddressFromLocation(location);
                        } else {
                            currentLocationTextView.setText("Unable to fetch location");
                        }
                    }
                });
    }

    private void getAddressFromLocation(Location location) {
        try {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String fullAddress = address.getAddressLine(0); // Get the complete address
                currentLocationTextView.setText(fullAddress);
            } else {
                currentLocationTextView.setText("Unable to fetch address");
            }
        } catch (Exception e) {
            e.printStackTrace();
            currentLocationTextView.setText("Error: Unable to fetch address");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            // Heart Rate Sensor event
            currentHeartRate = event.values[0];
            heartRateTextView.setText("Heart Rate: " + currentHeartRate + " bpm");
        } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            // Step Counter Sensor event
            stepsWalked = (int) event.values[0]; // value is the cumulative step count
            stepsTextView.setText("   Steps Walked:\n" + stepsWalked);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle sensor accuracy changes if needed
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Handle Permission Result
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchCurrentLocation();
        } else {
            currentLocationTextView.setText("Permission Denied");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister sensor listeners to prevent memory leaks
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Re-register sensor listeners to listen for sensor updates when the fragment is resumed
        if (heartRateSensor != null) {
            sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_UI);
        }
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }
}
