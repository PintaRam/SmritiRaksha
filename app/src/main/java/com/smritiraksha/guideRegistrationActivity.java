package com.smritiraksha;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class guideRegistrationActivity extends AppCompatActivity {

    private TextInputEditText etGuideName, etGuideId, etGuideEmail, etGuideContact, etAssociatedPatientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_registration);

        // Bind views with IDs from the layout
        etGuideName = findViewById(R.id.et_guide_name);
        etGuideId = findViewById(R.id.et_guide_id);
        etGuideEmail = findViewById(R.id.et_guide_email);
        etGuideContact = findViewById(R.id.et_guide_contact);
        etAssociatedPatientId = findViewById(R.id.et_associated_patient_id);

        // Set click listener for the registration button
        findViewById(R.id.btn_guide_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    // Proceed with registration or next steps
                    Toast.makeText(guideRegistrationActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Validate all inputs
    private boolean validateInputs() {
        // Get input values
        String guideName = etGuideName.getText().toString().trim();
        String guideId = etGuideId.getText().toString().trim();
        String guideEmail = etGuideEmail.getText().toString().trim();
        String guideContact = etGuideContact.getText().toString().trim();
        String associatedPatientId = etAssociatedPatientId.getText().toString().trim();

        // Check for empty fields and apply specific validation rules
        if (TextUtils.isEmpty(guideName)) {
            etGuideName.setError("Guide Name is required");
            etGuideName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(guideId)) {
            etGuideId.setError("Guide ID is required");
            etGuideId.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(guideEmail) || !Patterns.EMAIL_ADDRESS.matcher(guideEmail).matches()) {
            etGuideEmail.setError("Enter a valid email");
            etGuideEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(guideContact) || !guideContact.matches("^\\d{10}$")) {
            etGuideContact.setError("Enter a valid 10-digit contact number");
            etGuideContact.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(associatedPatientId)) {
            etAssociatedPatientId.setError("Associated Patient ID is required");
            etAssociatedPatientId.requestFocus();
            return false;
        }

        return true;
    }
}
