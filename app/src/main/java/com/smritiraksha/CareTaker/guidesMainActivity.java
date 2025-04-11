package com.smritiraksha.CareTaker;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smritiraksha.Constants;
import com.smritiraksha.R;

import org.json.JSONException;
import org.json.JSONObject;

public class guidesMainActivity extends AppCompatActivity {

    private static final String TAG = "GuidesMainActivity";
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guides_main);

        // Initialize Views
        drawerLayout = findViewById(R.id.drawer_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        ImageButton btnProfile = findViewById(R.id.btn_profile);

        // Set default fragment (Guide Dashboard)
        loadFragment(new DashboardFragmentguide());

        // Bottom Navigation Click Listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.navigation_dashboard) {
                    selectedFragment = new DashboardFragmentguide();
                } else if (item.getItemId() == R.id.navigation_live_tracking) {
                    selectedFragment = new tracking_nav();
                } else if (item.getItemId() == R.id.navigation_reminder) {
                    selectedFragment = new remainder_guide();
                } else if (item.getItemId() == R.id.navigation_emergency) {
                    selectedFragment = new emergency_guide();
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });

        // Click Listener for Profile Button
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleProfileDrawer();
            }
        });

        // Fetch User and Patient Details
        fetchPatientAndGuideDetails("PT01Sri", "guide2@gmail.com");
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void fetchPatientAndGuideDetails(String patientId, String guideEmail) {
        String url = Constants.FETCH_PATIENT_GUIDES_URL + "?patient_id=" + patientId + "&guide_email=" + guideEmail;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());
                        try {
                            if (response.has("success") && response.getBoolean("success")) {
                                if (response.has("data")) {
                                    JSONObject data = response.getJSONObject("data");
                                    String guideId = data.optString("guide_id", "N/A");
                                    String guideName = data.optString("guide_name", "N/A");
                                    String guideContact = data.optString("guide_contact", "N/A");
                                    String patientName = data.optString("patient_name", "N/A");
                                    String patientId = data.optString("patient_id", "N/A");
                                    String contact = data.optString("contact", "N/A");
                                    String age = String.valueOf(data.optInt("age", 0));
                                    String gender = data.optString("gender", "N/A");
                                    String email = data.optString("email", "N/A");

                                    openProfileFragment(guideId, guideName, guideContact,
                                            patientName, patientId, contact, age, gender, email);
                                }
                            } else {
                                Log.e(TAG, "No data found");
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing Error", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Request Error", error);
            }
        });

        queue.add(request);
    }

    private void openProfileFragment(String guideId, String guideName, String guideContact, String patientName, String patientId,
                                     String contact, String age, String gender, String email) {
        guide_profile guideProfileFragment = new guide_profile();
        Bundle bundle = new Bundle();
        bundle.putString("guideId", guideId);
        bundle.putString("guideName", guideName);
        bundle.putString("guideContact", guideContact);
        bundle.putString("patientName", patientName);
        bundle.putString("patientId", patientId);
        bundle.putString("contact", contact);
        bundle.putString("age", age);
        bundle.putString("gender", gender);
        bundle.putString("email", email);
        guideProfileFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_drawer, guideProfileFragment);
        transaction.commit();
    }

    private void toggleProfileDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}