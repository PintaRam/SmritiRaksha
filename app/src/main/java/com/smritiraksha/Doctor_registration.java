package com.smritiraksha;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Doctor_registration extends AppCompatActivity {

    private TextInputLayout doctorNameLayout, doctorMobileLayout, patientIdLayout, guideIdLayout;
    private TextInputEditText doctorNameEditText, doctorMobileEditText, patientIdEditText, guideIdEditText;
    private MaterialButton confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_registration);

        // Apply window insets for edge-to-edge layout
        View rootLayout = findViewById(android.R.id.content); // Use the root layout of the activity
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        initializeViews();

        // Set click listener on Confirm button
        confirmButton.setOnClickListener(v -> validateInputs());
    }

    private void initializeViews() {
        doctorNameLayout = findViewById(R.id.doctorNameLayout);
        doctorNameEditText = findViewById(R.id.doctorNameEditText);

        doctorMobileLayout = findViewById(R.id.doctorMobileLayout);
        doctorMobileEditText = findViewById(R.id.doctorMobileEditText);

        patientIdLayout = findViewById(R.id.patientIdLayout);
        patientIdEditText = findViewById(R.id.patientIdEditText);

        guideIdLayout = findViewById(R.id.guideIdLayout);
        guideIdEditText = findViewById(R.id.guideIdEditText);

        confirmButton = findViewById(R.id.confirmButton);
    }

    private void validateInputs() {
        boolean isValid = true;

        // Validate Doctor Name
        String doctorName = doctorNameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(doctorName)) {
            doctorNameLayout.setError("Doctor Name is required!");
            isValid = false;
        } else {
            doctorNameLayout.setError(null);
        }

        // Validate Doctor Mobile Number
        String doctorMobile = doctorMobileEditText.getText().toString().trim();
        if (TextUtils.isEmpty(doctorMobile)) {
            doctorMobileLayout.setError("Doctor Mobile Number is required!");
            isValid = false;
        } else if (!doctorMobile.matches("^[6-9]\\d{9}$")) { // Regular expression for 10-digit numbers starting with 6-9
            doctorMobileLayout.setError("Enter a valid 10-digit mobile number starting with 6-9!");
            isValid = false;
        } else {
            doctorMobileLayout.setError(null);
        }

        // Validate Patient ID
        String patientId = patientIdEditText.getText().toString().trim();
        if (TextUtils.isEmpty(patientId)) {
            patientIdLayout.setError("Patient ID is required!");
            isValid = false;
        } else {
            patientIdLayout.setError(null);
        }

        // Validate Guide ID
        String guideId = guideIdEditText.getText().toString().trim();
        if (TextUtils.isEmpty(guideId)) {
            guideIdLayout.setError("Guide ID is required!");
            isValid = false;
        } else {
            guideIdLayout.setError(null);
        }

        // If all inputs are valid
        if (isValid) {
            Toast.makeText(this, "All inputs are valid!", Toast.LENGTH_SHORT).show();
            // Proceed with your logic, such as saving data or navigating to another activity
        }
    }
}
