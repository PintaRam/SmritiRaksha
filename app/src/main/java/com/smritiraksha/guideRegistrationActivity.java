package com.smritiraksha;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class guideRegistrationActivity extends AppCompatActivity {

    private TextInputEditText guideNameEditText, guideIdEditText, guideEmailEditText, patientIdEditText, patientNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_registration);
        guideNameEditText = findViewById(R.id.guideNameEditText);
        guideIdEditText = findViewById(R.id.guideIdEditText);
        guideEmailEditText = findViewById(R.id.guideEmailEditText);
        patientIdEditText = findViewById(R.id.patientIdEditText);
        patientNameEditText = findViewById(R.id.patientNameEditText);

        findViewById(R.id.registerGuideButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    // Proceed with registration or next steps
                    Toast.makeText(guideRegistrationActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateInputs() {
        // Get input values
        String guideName = guideNameEditText.getText().toString().trim();
        String guideId = guideIdEditText.getText().toString().trim();
        String guideEmail = guideEmailEditText.getText().toString().trim();
        String patientId = patientIdEditText.getText().toString().trim();
        String patientName = patientNameEditText.getText().toString().trim();

        // Check for empty fields
        if (TextUtils.isEmpty(guideName)) {
            guideNameEditText.setError("Guide Name is required");
            guideNameEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(guideId)) {
            guideIdEditText.setError("Guide ID is required");
            guideIdEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(guideEmail) || !Patterns.EMAIL_ADDRESS.matcher(guideEmail).matches()) {
            guideEmailEditText.setError("Enter a valid email");
            guideEmailEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(patientId)) {
            patientIdEditText.setError("Patient ID is required");
            patientIdEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(patientName)) {
            patientNameEditText.setError("Patient Name is required");
            patientNameEditText.requestFocus();
            return false;
        }

        return true;
    }
}
