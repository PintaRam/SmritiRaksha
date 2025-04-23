package com.smritiraksha.Doctor;

import static android.content.Intent.getIntent;

import android.app.AlertDialog;
import android.app.usage.ConfigurationStats;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.smritiraksha.Constants;
import com.smritiraksha.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Doctor_registration extends AppCompatActivity {

    private TextInputLayout doctorNameLayout, doctorMobileLayout, patientIdLayout, HospitalLayout;
    private TextInputEditText doctorNameEditText, doctorMobileEditText, patientIdEditText, HospitalEditText;
    private MaterialButton confirmButton;
    private String DocEmail="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_registration);
        Intent intent = getIntent();
        DocEmail = intent.getStringExtra("userEmail");
        // Apply window insets for edge-to-edge layout
        View rootLayout = findViewById(android.R.id.content); // Use the root layout of the activity
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        initializeViews();
        patientIdEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchAllPatients();
            }
        });

        // Set click listener on Confirm button
        confirmButton.setOnClickListener(v -> StoreDoctorData());
    }

    private void initializeViews() {
        doctorNameLayout = findViewById(R.id.doctorNameLayout);
        doctorNameEditText = findViewById(R.id.doctorNameEditText);

        doctorMobileLayout = findViewById(R.id.doctorMobileLayout);
        doctorMobileEditText = findViewById(R.id.doctorMobileEditText);

        patientIdLayout = findViewById(R.id.patientIdLayout);
        patientIdEditText = findViewById(R.id.patientIdEditText);

        HospitalLayout = findViewById(R.id.HospnameLayout);
        HospitalEditText = findViewById(R.id.HosnameEditText);

        confirmButton = findViewById(R.id.confirmButton);
    }


    private void fetchAllPatients() {
        String url = Constants.All_Patinets;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (!jsonResponse.getBoolean("error")) {
                            JSONArray patientsArray = jsonResponse.getJSONArray("patients");

                            List<String> displayList = new ArrayList<>();
                            List<String> idList = new ArrayList<>();

                            for (int i = 0; i < patientsArray.length(); i++) {
                                JSONObject patient = patientsArray.getJSONObject(i);
                                String name = patient.getString("patient_name");
                                String id = patient.getString("patient_id");

                                // For display
                                displayList.add(name + " (" + id + ")");
                                // For storing ID
                                idList.add(id);
                            }

                            showPatientDialog(displayList, idList);
                        } else {
                            Toast.makeText(this, "Failed: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Volley Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        queue.add(stringRequest);
    }
    private void showPatientDialog(List<String> displayList, List<String> idList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Patient");

        String[] displayNames = displayList.toArray(new String[0]);

        builder.setItems(displayNames, (dialog, which) -> {
            // Set patient ID in EditText
            patientIdEditText.setText(idList.get(which));
        });

        builder.show();
    }


    private void StoreDoctorData() {
            boolean isValid = true;

            String doctorName = doctorNameEditText.getText().toString().trim();
            String doctorMobile = doctorMobileEditText.getText().toString().trim();
            String patientId = patientIdEditText.getText().toString().trim();
            String hospitalName = HospitalEditText.getText().toString().trim();
            String email = DocEmail; // Assuming DocEmail is already initialized

            // Validate Doctor Name
            if (TextUtils.isEmpty(doctorName)) {
                doctorNameLayout.setError("Doctor Name is required!");
                isValid = false;
            } else {
                doctorNameLayout.setError(null);
            }

            // Validate Mobile Number
            if (TextUtils.isEmpty(doctorMobile)) {
                doctorMobileLayout.setError("Doctor Mobile Number is required!");
                isValid = false;
            } else if (!doctorMobile.matches("^[6-9]\\d{9}$")) {
                doctorMobileLayout.setError("Enter a valid 10-digit mobile number starting with 6-9!");
                isValid = false;
            } else {
                doctorMobileLayout.setError(null);
            }

            // Validate Patient ID
            if (TextUtils.isEmpty(patientId)) {
                patientIdLayout.setError("Patient ID is required!");
                isValid = false;
            } else {
                patientIdLayout.setError(null);
            }

            // Validate Hospital Name
            if (TextUtils.isEmpty(hospitalName)) {
                HospitalLayout.setError("Hospital Name is required!");
                isValid = false;
            } else {
                HospitalLayout.setError(null);
            }

            if (!isValid) return;

            // Proceed to store if valid
            String url = Constants.Doc_Register;

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String message = jsonResponse.getString("message");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                            if (!jsonResponse.getBoolean("error")) {
                                // Navigate to DoctorDashboard
                                Intent intent = new Intent(Doctor_registration.this, DoctorDashboard.class);
                                intent.putExtra("userEmail", DocEmail); // pass email if needed
                                startActivity(intent);
                                finish(); // optional: closes the current activity
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Invalid response", Toast.LENGTH_SHORT).show();
                        }
                    },

                    error -> Toast.makeText(this, "Volley Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("DocName", doctorName);
                    params.put("DocEmail", email);
                    params.put("MobileNum", doctorMobile);
                    params.put("HospitalName", hospitalName);
                    params.put("PatientID", patientId); // ✅ Ensure key name matches PHP script
                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(stringRequest);
    }

}
