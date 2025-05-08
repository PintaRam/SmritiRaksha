package com.smritiraksha.Patient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smritiraksha.Constants;
import com.smritiraksha.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private JSONObject userDetails;
    private MediaPlayer mediaPlayer;
    private ImageButton profileButton;
    private String email;// = "patient1@gmail.com"; // Replace dynamically if needed
    private EmergencyReceiver emergencyReceiver;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable emergencyCheckRunnable = new Runnable() {
        @Override
        public void run() {
            checkEmergencyStatus(); // Calls API to check emergency status
            handler.postDelayed(this, 5000); // Schedule next check in 5 seconds
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment(new DashboardFragment());
        email=getIntent().getStringExtra("PatEmail");

        drawerLayout = findViewById(R.id.drawer_layout);
        profileButton = findViewById(R.id.btn_profile);
        profileButton.setEnabled(false);

        profileButton.setOnClickListener(view -> toggleProfileDrawer());

        // Bottom Navigation
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_dashboard) {
                loadFragment(new DashboardFragment());
            } else if (item.getItemId() == R.id.nav_tracking) {
                loadFragment(new TrackingFragment());
            } else if (item.getItemId() == R.id.nav_reminder) {
                MedicalReminderFragment reminderFragment = new MedicalReminderFragment();
                Bundle args = new Bundle();
                args.putString("patient_email", email);  // send the patient email
                reminderFragment.setArguments(args);
                loadFragment(reminderFragment);
            } else if (item.getItemId() == R.id.nav_emergency) {
                loadFragment(new EmergencyFragment());
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Fetch User Details
        fetchUserDetails(email);

        // Start Emergency Polling
        startEmergencyPolling();

        // Register the emergency receiver
        emergencyReceiver = new EmergencyReceiver();
        IntentFilter filter = new IntentFilter("com.smritiraksha.EMERGENCY_ALERT");
//        registerReceiver(emergencyReceiver, filter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(emergencyReceiver, filter, Context.RECEIVER_EXPORTED);
        }

    }

    private void fetchUserDetails(String email) {
        String url = Constants.FETCH_PATIENT_URL + "?email=" + email;

        // Create a GET request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "User Details Fetched: " + response);
                    userDetails = response.optJSONObject("patient_data"); // Extract "patient_data" safely
                    if (userDetails == null) {
                        Toast.makeText(MainActivity.this, "No patient data found in response", Toast.LENGTH_SHORT).show();
                    } else {
                        profileButton.setEnabled(true); // Enable only when data is ready
                    }
                },
                error -> {
                    // Improved error logging
                    Log.e(TAG, "Error Fetching User Details: ", error);

                    // Additional check on the error type
                    if (error instanceof com.android.volley.NetworkError) {
                        Toast.makeText(MainActivity.this, "Network error occurred.", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof com.android.volley.ServerError) {
                        Toast.makeText(MainActivity.this, "Server error occurred.", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof com.android.volley.ClientError) {
                        Toast.makeText(MainActivity.this, "Client error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Unexpected error occurred.", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the request queue
        Volley.newRequestQueue(this).add(request);
    }


    private void toggleProfileDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (userDetails != null) {
                try {
                    String patientId = getSafeJsonValue(userDetails, "patient_id");
                    String patientName = getSafeJsonValue(userDetails, "patient_name");
                    String patientContact = getSafeJsonValue(userDetails, "contact");
                    String patientAge = getSafeJsonValue(userDetails, "age");
                    String patientGender = getSafeJsonValue(userDetails, "gender");
                    String patientEmail = getSafeJsonValue(userDetails, "email");
                    String guideId = getSafeJsonValue(userDetails, "guide_id");
                    String guideName = getSafeJsonValue(userDetails, "guide_name");

                    if (patientId.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Patient ID is missing", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ProfileFragment profileFragment = ProfileFragment.newInstance(
                            patientId, patientName, patientContact, patientAge,
                            patientGender, patientEmail, guideId, guideName
                    );

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.profile_drawer, profileFragment)
                            .commit();
                    drawerLayout.openDrawer(GravityCompat.START);

                } catch (Exception e) {
                    Log.e(TAG, "Error Setting Profile Details: ", e);
                    Toast.makeText(MainActivity.this, "Error displaying profile details", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Loading user details, please wait...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void startEmergencyPolling() {
        handler.post(emergencyCheckRunnable);
    }

    private void stopEmergencyPolling() {
        handler.removeCallbacks(emergencyCheckRunnable);
    }

    private void checkEmergencyStatus() {
        String url = Constants.CHECK_EMERGENCY;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.trim());
                        boolean error = jsonResponse.getBoolean("error");

                        if (!error) {
                            String message = jsonResponse.getString("message");
                            boolean isEmergency = message.equalsIgnoreCase("Emergency triggered!");
                            sendEmergencyBroadcast(isEmergency);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                    }
                },
                error -> Log.e(TAG, "Volley Error: " + error.getMessage())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("patient_email", email);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void sendEmergencyBroadcast(boolean isEmergency) {
        Intent intent = new Intent("com.smritiraksha.EMERGENCY_ALERT");
        intent.putExtra("isEmergency", isEmergency);
        sendBroadcast(intent);
    }

    public void playEmergencyAlarm(Context context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.sos_sound);
            mediaPlayer.setLooping(true);
        }

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Log.d(TAG, "Emergency alarm started.");
        }
    }

    public void stopEmergencyAlarm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d(TAG, "Emergency alarm stopped.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopEmergencyPolling();
        unregisterReceiver(emergencyReceiver);
    }

    private class EmergencyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isEmergency = intent.getBooleanExtra("isEmergency", false);
            if (isEmergency) {
                playEmergencyAlarm(context);
            } else {
                stopEmergencyAlarm();
            }
        }
    }
    private String getSafeJsonValue(JSONObject jsonObject, String key) {
        try {
            return jsonObject.has(key) ? jsonObject.getString(key) : "";
        } catch (JSONException e) {
            Log.e(TAG, "Missing or Invalid Key: " + key, e);
            return "";
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.hasExtra("fragmentToLoad")) {
            String tag = intent.getStringExtra("fragmentToLoad");
            if ("MedicalReminderFragment".equals(tag)) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new MedicalReminderFragment())
                        .commit();
            }
        }
    }
}
