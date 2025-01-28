package com.smritiraksha;

import android.annotation.SuppressLint;
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

public class guidesMainActivity extends AppCompatActivity {

    private static final String TAG = "guidesMainActivity";
    private DrawerLayout drawerLayout;
    private JSONObject guideDetails;

    private String email;
    private String patientId; // Store the patient ID globally in this activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guides_main);

        // Retrieve Intent data
        Intent intent = getIntent();
        email = intent.getStringExtra("userEmail"); // Retrieve the email passed to this activity
        patientId = intent.getStringExtra("PATIENT_ID"); // Retrieve the patient ID from the Intent

        // Debugging Log
        Log.d(TAG, "Email: " + email + ", Patient ID: " + patientId);

        // Show a toast for testing
        if (patientId != null && !patientId.isEmpty()) {
            Toast.makeText(this, "Patient ID: " + patientId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No Patient ID found!", Toast.LENGTH_SHORT).show();
        }

        // Initialize DrawerLayout and Profile Button
        drawerLayout = findViewById(R.id.drawer_layout);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageButton profileButton = findViewById(R.id.btn_profile);

        // Set click listener to toggle the profile drawer
        profileButton.setOnClickListener(view -> toggleProfileDrawer());

        // Bottom Navigation setup
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment;
            if (item.getItemId() == R.id.navigation_dashboard) {
                selectedFragment = new DashboardFragmentguide();
            } else if (item.getItemId() == R.id.navigation_live_tracking) {
                selectedFragment = new tracking_nav();
            } else if (item.getItemId() == R.id.navigation_reminder) {
                selectedFragment = new remainder_guide();
            } else if (item.getItemId() == R.id.navigation_emergency) {
                selectedFragment = new emergency_guide();
            } else {
                return false; // Stop further handling for invalid IDs
            }

            // Pass Patient ID to Fragment (if needed) and Load it
            Bundle bundle = new Bundle();
            bundle.putString("PATIENT_ID", patientId); // Pass the patient ID to the selected fragment
            selectedFragment.setArguments(bundle);

            loadFragment(selectedFragment);
            drawerLayout.closeDrawer(GravityCompat.START); // Close Drawer
            return true; // Indicate the item was handled
        });

        // Default Fragment
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragmentguide()); // Load Dashboard as default
        }

        // Optionally fetch guide details
        // fetchGuideDetails(email);
    }

    // Toggles the profile drawer open or close
    private void toggleProfileDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
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
