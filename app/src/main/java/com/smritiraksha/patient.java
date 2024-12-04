package com.smritiraksha;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

public class patient extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient);  // Ensure layout name matches

        // Input Fields
        TextInputEditText etPatientName = findViewById(R.id.et_patient_name);
        TextInputEditText etPatientID = findViewById(R.id.et_patient_id);
        TextInputEditText etContact = findViewById(R.id.et_contact);
        AutoCompleteTextView actSex = findViewById(R.id.act_sex);
        AutoCompleteTextView actGuideID = findViewById(R.id.act_guide_id);
        AutoCompleteTextView actGuideName = findViewById(R.id.act_guide_name);

        // Submit Button
        MaterialButton btnSubmit = findViewById(R.id.btn_submit);

        // Dropdown Data
        String[] sexOptions = {"Male", "Female", "Other"};
        String[] guideIDs = {"G101", "G102", "G103"};
        String[] guideNames = {"John Doe", "Jane Smith", "Michael Brown"};

        // Adapters for Dropdowns
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sexOptions);
        ArrayAdapter<String> guideIDAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, guideIDs);
        ArrayAdapter<String> guideNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, guideNames);

        actSex.setAdapter(sexAdapter);
        actGuideID.setAdapter(guideIDAdapter);
        actGuideName.setAdapter(guideNameAdapter);

        // Submit Button Click Listener
        btnSubmit.setOnClickListener(v -> {
            String patientName = etPatientName.getText().toString();
            String patientID = etPatientID.getText().toString();
            String contact = etContact.getText().toString();
            String sex = actSex.getText().toString();
            String guideID = actGuideID.getText().toString();
            String guideName = actGuideName.getText().toString();

            boolean isValid = true;

            // Validate Patient Name
            if (patientName.isEmpty()) {
                etPatientName.setError("Patient Name is required");
                isValid = false;
            } else {
                etPatientName.setError(null); // Clear error
            }

            // Validate Patient ID
            if (patientID.isEmpty()) {
                etPatientID.setError("Patient ID is required");
                isValid = false;
            } else {
                etPatientID.setError(null); // Clear error
            }

            // Validate Contact
            if (contact.isEmpty()) {
                etContact.setError("Contact is required");
                isValid = false;
            } else {
                etContact.setError(null); // Clear error
            }

            // Validate Gender (Dropdown)
            if (sex.isEmpty()) {
                Toast.makeText(patient.this, "Gender is required", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            // Validate Guide ID (Dropdown)
            if (guideID.isEmpty()) {
                Toast.makeText(patient.this, "Guide ID is required", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            // Validate Guide Name (Dropdown)
            if (guideName.isEmpty()) {
                Toast.makeText(patient.this, "Guide Name is required", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            // Check if all fields are valid
            if (isValid) {
                // Success: Handle form submission
                Toast.makeText(patient.this, "Registration Successful", Toast.LENGTH_SHORT).show();
            } else {
                // Failure: Notify user to fill required fields
                Toast.makeText(patient.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
