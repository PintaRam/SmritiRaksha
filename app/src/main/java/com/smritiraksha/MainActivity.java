package com.smritiraksha;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private String email;
    private JSONObject userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get email from the intent
//        Intent intent = getIntent();
//        email = intent.getStringExtra("userEmail");
//        Log.d("email", email);

        // Toolbar setup
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer Layout setup
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        // Profile button toggles the drawer
        ImageButton profileButton = findViewById(R.id.btn_profile);
        profileButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        // Handle navigation view item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile_settings) {
                // Handle Profile Settings item click
                if (userDetails != null) {
                    ProfileFragment fragment = new ProfileFragment();
                    Bundle args = new Bundle();
                    args.putString("userDetails", userDetails.toString());
                    fragment.setArguments(args);
                    loadFragment(fragment);
                } else {
                    Toast.makeText(this, "User details not loaded yet", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });

        // Bottom Navigation setup
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            for (int i = 0; i < bottomNavigation.getChildCount(); i++) {
                View view = bottomNavigation.getChildAt(i);
                view.animate().scaleX(1.0f).scaleY(1.0f).translationY(0).setDuration(200).start();
            }

            // Animate clicked item
            View selectedView = bottomNavigation.findViewById(item.getItemId());
            if (selectedView != null) {
                selectedView.animate().scaleX(1.2f).scaleY(1.2f).translationY(-10).setDuration(200).start();
            }

            // Load the correct fragment
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.nav_tracking) {
                selectedFragment = new TrackingFragment();
            } else if (item.getItemId() == R.id.nav_reminder) {
                selectedFragment = new MedicalReminderFragment();
            } else if (item.getItemId() == R.id.nav_dashboard) {
                selectedFragment = new DashboardFragment();
            } else if (item.getItemId() == R.id.nav_emergency) {
                selectedFragment = new EmergencyFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });

        // Default fragment: Dashboard
        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
            loadFragment(new DashboardFragment());
        }

        // Fetch user details
        fetchUserDetails(email);
    }

    private void fetchUserDetails(String email) {
        String url = Constants.FETCH_PATIENT_URL + "?email=" + email;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        userDetails = response; // Store the details for later use
                        Log.d("MainActivity", "User details fetched successfully: " + userDetails.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error fetching user details: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
