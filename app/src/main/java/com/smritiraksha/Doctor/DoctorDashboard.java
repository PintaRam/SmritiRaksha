package com.smritiraksha.Doctor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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

    private String docmail, patientName, patientId, patientEmail;

    private TextView DocEmail, DocMobile, hospital, headerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        profileIcon = findViewById(R.id.profileIcon);

        docmail = getIntent().getStringExtra("doemail");
        patientId = getIntent().getStringExtra("patient_id");
        patientName = getIntent().getStringExtra("patient_name");

        // Fetch patient email before setting up adapter
        fetchPatientDetails(patientId);

        // Inflate doctor info layout and add to navigation view
        ViewGroup navContainer = (ViewGroup) navigationView;
        infoView = getLayoutInflater().inflate(R.layout.drawer_doctor_info, navContainer, false);
        navContainer.addView(infoView);

        profileIcon.setOnClickListener(v -> {
            drawerLayout.openDrawer(navigationView);
            fetchDoctorDetails(docmail);
        });

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

    private void fetchPatientDetails(String Id) {
        String url = Constants.FETCH_PATIENT_BYID;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "?id=" + Id,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean error = jsonResponse.getBoolean("error");

                        if (!error) {
                            JSONObject patientData = jsonResponse.getJSONObject("patient_data");
                            patientEmail = patientData.getString("email");
                            Log.d("DoctorDashboard PEmail",patientEmail);

                            adapter = new DashboardAdapter(this, docmail, patientId, patientName, patientEmail);
                            viewPager.setAdapter(adapter);

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

                            Log.d("DoctorDashboard", "Patient email fetched: " + patientEmail);
                        } else {
                            Toast.makeText(this, "Error fetching patient data", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to parse data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    public void fetchDoctorDetails(String email) {
        String url = Constants.Doc_Info_API;

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

                            View headerView = navigationView.getHeaderView(0);
                            if (headerView != null) {
                                headerName = headerView.findViewById(R.id.doctorName);
                                DocEmail = infoView.findViewById(R.id.tvDoctorEmail);
                                DocMobile = infoView.findViewById(R.id.tvDoctorMobile);
                                hospital = infoView.findViewById(R.id.tvHospitalName);

                                if (headerName != null) headerName.setText("Dr. " + name);
                                if (DocEmail != null) DocEmail.setText(emailStr);
                                if (DocMobile != null) DocMobile.setText(mobileStr);
                                if (hospital != null) hospital.setText(hospitalStr);
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
                params.put("docemail", email);
                return params;
            }
        };

        queue.add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            String fragmentName = data.getStringExtra("return_to_fragment");
            if ("MedicationFragment".equals(fragmentName)) {
                loadMedicationFragment();
            }
        }
    }

    private void loadMedicationFragment() {
        MedicationFragment medicationFragment = new MedicationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("docmail", docmail);
        bundle.putString("patientId", patientId);
        bundle.putString("patientName", patientName);
        bundle.putString("patientEmail", patientEmail);
        medicationFragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, medicationFragment)
                .commit();
    }
}
