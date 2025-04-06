package com.smritiraksha.Patient;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.smritiraksha.Patient.Games.LogicPuzzleSplash;
import com.smritiraksha.Patient.Games.Wordsearchsplash;
import com.smritiraksha.Patient.Games.memoryGameSplash;
import com.smritiraksha.Patient.Games.sudokuSplash;
import com.smritiraksha.R;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DashboardFragment extends Fragment {
    private static final int GOOGLE_FIT_REQUEST_CODE = 1;
    private TextView currentLocationTextView;
    private TextView heartRateTextView;
    private TextView stepsTextView;
    private TextView sleepDurationTextView;
    private TextView weightTextView;
    private Button wordSearchButton;

    private int stepsWalked = 0;
    private float currentHeartRate = 0;
    private float weight = 0;
    private long sleepDuration = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentLocationTextView = view.findViewById(R.id.current_location_text);
        heartRateTextView = view.findViewById(R.id.heart_rate_text);
        stepsTextView = view.findViewById(R.id.steps_text);
        sleepDurationTextView = view.findViewById(R.id.sleep_text);
        weightTextView = view.findViewById(R.id.weight_text);
        wordSearchButton = view.findViewById(R.id.word_search_play_button);

        wordSearchButton.setOnClickListener(v -> {
            Intent wrdsrch = new Intent(requireContext(), Wordsearchsplash.class);
            startActivity(wrdsrch);
        });

        fetchCurrentLocation();

        // Button logic for games
        Button logicPlayButton = view.findViewById(R.id.logic_puzzle_play_button);
        logicPlayButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LogicPuzzleSplash.class);
            startActivity(intent);
        });

        Button sudokuPlayButton = view.findViewById(R.id.sudoku_play_button);
        sudokuPlayButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), sudokuSplash.class);
            startActivity(intent);
        });


        // Request Google Fit permissions and fetch data
        requestGoogleFitPermissions();

        Button memPlayButton = view.findViewById(R.id.memory_game_play_button);
        memPlayButton.setOnClickListener(v -> {
            // Start memory game Splash Activity
            Intent mintent = new Intent(requireContext(), memoryGameSplash.class);
            startActivity(mintent);
        });

    }

    private void fetchCurrentLocation() {
        // Check if the necessary permissions are granted
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        // Use FusedLocationProviderClient to fetch the current location
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Check if the location is null
                        if (location != null) {
                            // Call the method to get the address from location
                            getAddressFromLocation(location);
                        } else {
                            currentLocationTextView.setText("Unable to fetch location.");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    currentLocationTextView.setText("Error fetching location.");
                });
    }

    private void getAddressFromLocation(Location location) {
        try {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String fullAddress = address.getAddressLine(0);
                currentLocationTextView.setText(fullAddress);
            } else {
                currentLocationTextView.setText("Unable to fetch address");
            }
        } catch (Exception e) {
            e.printStackTrace();
            currentLocationTextView.setText("Error: Unable to fetch address");
        }
    }

    private void requestGoogleFitPermissions() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
        if (account == null) {
            Intent signInIntent = GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN).getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_FIT_REQUEST_CODE);
        } else {
            fetchGoogleFitData(account);
        }
    }

    private void fetchGoogleFitData(GoogleSignInAccount account) {
        long endTime = System.currentTimeMillis();
        long startTime = endTime - (24 * 60 * 60 * 1000); // 24 hours ago

        // Fetch steps
        Fitness.getHistoryClient(requireContext(), account)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnCompleteListener(new OnCompleteListener<DataSet>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSet> task) {
                        if (task.isSuccessful()) {
                            DataSet dataSet = task.getResult();
                            if (dataSet != null && !dataSet.getDataPoints().isEmpty()) {
                                stepsWalked = dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                                stepsTextView.setText("Steps Walked: \n" + stepsWalked);
                            } else {
                                stepsTextView.setText("No steps data available.");
                            }
                        } else {
                            stepsTextView.setText("Failed to fetch steps");
                        }
                    }
                });

        // Fetch heart rate
        Fitness.getHistoryClient(requireContext(), account)
                .readData(new DataReadRequest.Builder()
                        .read(DataType.TYPE_HEART_RATE_BPM)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build())
                .addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<DataReadResponse> task) {
                        if (task.isSuccessful()) {
                            DataReadResponse response = task.getResult();
                            if (response != null) {
                                DataSet dataSet = response.getDataSet(DataType.TYPE_HEART_RATE_BPM);
                                if (dataSet != null && !dataSet.getDataPoints().isEmpty()) {
                                    currentHeartRate = dataSet.getDataPoints().get(0).getValue(Field.FIELD_BPM).asFloat();
                                    heartRateTextView.setText("Heart Rate: \n" + currentHeartRate + " bpm");
                                } else {
                                    heartRateTextView.setText("No heart rate data available.");
                                }
                            } else {
                                heartRateTextView.setText("Failed to fetch heart rate");
                            }
                        } else {
                            heartRateTextView.setText("Failed to fetch heart rate");
                        }
                    }
                });

        // Fetch sleep data
        Fitness.getHistoryClient(requireContext(), account)
                .readData(new DataReadRequest.Builder()
                        .read(DataType.TYPE_SLEEP_SEGMENT)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build())
                .addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<DataReadResponse> task) {
                        if (task.isSuccessful()) {
                            DataReadResponse response = task.getResult();
                            if (response != null) {
                                DataSet dataSet = response.getDataSet(DataType.TYPE_SLEEP_SEGMENT);
                                if (dataSet != null && !dataSet.getDataPoints().isEmpty()) {
                                    sleepDuration = dataSet.getDataPoints().get(0).getEndTime(TimeUnit.MILLISECONDS) -
                                            dataSet.getDataPoints().get(0).getStartTime(TimeUnit.MILLISECONDS);
                                    sleepDurationTextView.setText("Sleep Duration: \n" + sleepDuration / (60 * 1000) + " minutes");
                                } else {
                                    sleepDurationTextView.setText("No sleep data available.");
                                }
                            } else {
                                sleepDurationTextView.setText("Failed to fetch sleep data");
                            }
                        } else {
                            sleepDurationTextView.setText("Failed to fetch sleep data");
                        }
                    }
                });

        // Fetch weight data
        Fitness.getHistoryClient(requireContext(), account)
                .readData(new DataReadRequest.Builder()
                        .read(DataType.TYPE_WEIGHT)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build())
                .addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<DataReadResponse> task) {
                        if (task.isSuccessful()) {
                            DataReadResponse response = task.getResult();
                            if (response != null) {
                                DataSet dataSet = response.getDataSet(DataType.TYPE_WEIGHT);
                                if (dataSet != null && !dataSet.getDataPoints().isEmpty()) {
                                    weight = dataSet.getDataPoints().get(0).getValue(Field.FIELD_WEIGHT).asFloat();
                                    weightTextView.setText("Weight: \n" + weight + " kg");
                                } else {
                                    weightTextView.setText("No weight data available.");
                                }
                            } else {
                                weightTextView.setText("Failed to fetch weight");
                            }
                        } else {
                            weightTextView.setText("Failed to fetch weight");
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_FIT_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
            if (account != null) {
                fetchGoogleFitData(account);
            }
        }
    }
}
