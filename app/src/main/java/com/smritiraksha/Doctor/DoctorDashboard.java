package com.smritiraksha.Doctor;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.smritiraksha.Constants;
import com.smritiraksha.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DoctorDashboard extends AppCompatActivity {
        private ViewPager2 viewPager;
        private TabLayout tabLayout;
        private DashboardAdapter adapter;
        private DrawerLayout drawerLayout;
        private NavigationView navigationView;
        private ImageView profileIcon;
        private View infoView;
    private TextView DocEmail,DocMobile,hospital,headerName;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_doctor_dashboard);

            String docmail=getIntent().getStringExtra("doemail");

            String patientId = getIntent().getStringExtra("patient_id");
            String patientName = getIntent().getStringExtra("patient_name");

            viewPager = findViewById(R.id.viewPager);
            tabLayout = findViewById(R.id.tabLayout);
            drawerLayout = findViewById(R.id.drawerLayout);
            navigationView = findViewById(R.id.navigationView);
            profileIcon = findViewById(R.id.profileIcon);



            // Inflate doctor info layout and add below header
            ViewGroup navContainer = (ViewGroup) navigationView;
            infoView = getLayoutInflater().inflate(R.layout.drawer_doctor_info, navContainer, false);
            navContainer.addView(infoView);

            adapter = new DashboardAdapter(this);
            viewPager.setAdapter(adapter);
            profileIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(navigationView);
                    fetchDoctorDetails("Te@gmail.com");
                }
            });

            new TabLayoutMediator(tabLayout, viewPager,
                    (tab, position) -> {
                        switch (position) {
                            case 0:
                                tab.setIcon(R.drawable.ic_overview);
                                tab.setText("Overview");
                                break;
                            case 1:
                                tab.setIcon(R.drawable.ic_medication);
                                tab.setText("Medication");
                                break;
                            case 2:
                                tab.setIcon(R.drawable.ic_alert);
                                tab.setText("Alerts");
                                break;
                        }
                    }).attach();


            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_logout) {
                    // handle logout
                } else if (id == R.id.nav_settings) {
                    // handle settings
                }

                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            });

        }
    // This method will be responsible for setting the doctor details in the navigation drawer
    public void fetchDoctorDetails(String email) {
        String url = Constants.Doc_Info_API; // Update with your actual URL

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean status = jsonResponse.getBoolean("status");

                        if (status) {
                            JSONObject doctor = jsonResponse.getJSONObject("doctor");
                            String name = doctor.optString("DocName", "N/A");
                            String emailStr = doctor.optString("DocEmail", "N/A");
                            String mobileStr = doctor.optString("MobileNum", "N/A");
                            String hospitalStr = doctor.optString("HospitalName", "N/A");

                            // Inflate the doctor info layout into the NavigationView header
                            View headerView = navigationView.getHeaderView(0);
                            if (headerView != null) {
                                headerName = headerView.findViewById(R.id.doctorName);
                                DocEmail = infoView.findViewById(R.id.tvDoctorEmail);
                                DocMobile = infoView.findViewById(R.id.tvDoctorMobile);
                                hospital = infoView.findViewById(R.id.tvHospitalName);

                                // Null check before calling setText() on TextViews
                                if (headerName != null) {
                                    headerName.setText("Dr. " + name);
                                } else {
                                    Log.d("DoctorDashboard", "headerName is null");
                                    Toast.makeText(this, "Doctor name is not found", Toast.LENGTH_SHORT).show();
                                }

                                if (DocEmail != null) {
                                    DocEmail.setText(emailStr);
                                } else {
                                    Log.d("DoctorDashboard", "docemail is null");
                                    Toast.makeText(this, "Email TextView is not found", Toast.LENGTH_SHORT).show();
                                }

                                if (DocMobile != null) {
                                    DocMobile.setText(mobileStr);
                                } else {
                                    Log.d("DoctorDashboard", "docmobile is null");
                                    Toast.makeText(this, "Mobile TextView is not found", Toast.LENGTH_SHORT).show();
                                }

                                if (hospital != null) {
                                    hospital.setText(hospitalStr);
                                } else {
                                    Log.d("DoctorDashboard", "hospital is null");
                                    Toast.makeText(this, "Hospital TextView is not found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d("DoctorDashboard", "Header view is null");
                            }

                        } else {
                            String message = jsonResponse.getString("message");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error fetching doctor details", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("docemail", email); // Send the email to PHP
                return params;
            }
        };

        queue.add(request);
    }



}
