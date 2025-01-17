package com.smritiraksha;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private JSONObject userDetails;
    String email =  "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        email = intent.getStringExtra("userEmail");
        drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton profileButton = findViewById(R.id.btn_profile);

        profileButton.setOnClickListener(view -> toggleProfileDrawer());

        // Bottom Navigation
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_dashboard) {
                loadFragment(new DashboardFragment());
            } else if (item.getItemId() == R.id.nav_tracking) {
                loadFragment(new TrackingFragment());
            } else if (item.getItemId() == R.id.nav_reminder) {
                loadFragment(new MedicalReminderFragment());
            } else if (item.getItemId() == R.id.nav_emergency) {
                loadFragment(new EmergencyFragment());
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Default Fragment
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
        }

        // Fetch User Details
        fetchUserDetails(email); // Replace with dynamic email
    }

    private void fetchUserDetails(String email) {
        String url = Constants.FETCH_PATIENT_URL + "?email=" + email;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "User Details Fetched: " + response);
                    userDetails = response.optJSONObject("patient_data"); // Extract "patient_data" safely
                    if (userDetails == null) {
                        Toast.makeText(MainActivity.this, "No patient data found in response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error Fetching User Details: ", error);
                    Toast.makeText(MainActivity.this, "Error fetching user details: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

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

    /**
     * Safely retrieves a JSON value by key, returning an empty string if the key is missing or invalid.
     */
    private String getSafeJsonValue(JSONObject jsonObject, String key) {
        try {
            return jsonObject.has(key) ? jsonObject.getString(key) : "";
        } catch (JSONException e) {
            Log.e(TAG, "Missing or Invalid Key: " + key, e);
            return "";
        }
    }
}
