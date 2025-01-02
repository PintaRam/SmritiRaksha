package com.smritiraksha;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class guideRegistrationActivity extends AppCompatActivity {

    private TextInputEditText guideNameEditText, guideIdEditText, guideContactEditText, guideAddressEditText;
    private MaterialAutoCompleteTextView guideGenderAutoCompleteTextView;
    private ProgressDialog progressDialog;
    String guideEmail = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_registration);
        Intent intent = getIntent();
        guideEmail = intent.getStringExtra("userEmail");
        // Initialize views
        MaterialButton registerButton = findViewById(R.id.btn_guide_submit);
        guideNameEditText = findViewById(R.id.et_guide_name);
        guideIdEditText = findViewById(R.id.et_guide_id);
        guideContactEditText = findViewById(R.id.et_guide_contact);
        guideAddressEditText = findViewById(R.id.et_guide_address);
        guideGenderAutoCompleteTextView = findViewById(R.id.et_guide_gender);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering guide...");

        // Gender dropdown values
        String[] genderOptions = {"Male", "Female", "Other"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genderOptions);
        guideGenderAutoCompleteTextView.setAdapter(genderAdapter);

        // Show dropdown on focus
        guideGenderAutoCompleteTextView.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                guideGenderAutoCompleteTextView.showDropDown();
            }
        });

        // Add button click actions with animation
        registerButton.setOnClickListener(view -> {
            enhancedButtonAnimation(registerButton); // Button press animation
            registerGuide(); // Guide registration logic
        });
    }

    private void registerGuide() {
        String guideName = guideNameEditText.getText().toString().trim();
        String guideId = guideIdEditText.getText().toString().trim();
        String guideContact = guideContactEditText.getText().toString().trim();
        String guideAddress = guideAddressEditText.getText().toString().trim();
        String guideGender = guideGenderAutoCompleteTextView.getText().toString().trim();


        // Validation
        if (TextUtils.isEmpty(guideName)) {
            guideNameEditText.setError("Guide Name is required!");
            return;
        }
        if (TextUtils.isEmpty(guideId)) {
            guideIdEditText.setError("Guide ID is required!");
            return;
        }
        // Mobile number validation - checks for exactly 10 digits
        if (TextUtils.isEmpty(guideContact)) {
            guideContactEditText.setError("Contact number is required!");
            return;
        } else if (!guideContact.matches("^[6-9]\\d{9}$")) { // Mobile numbers must start with 6-9 in India
            guideContactEditText.setError("Enter a valid 10-digit contact number!");
            return;
        }
        if (TextUtils.isEmpty(guideAddress)) {
            guideAddressEditText.setError("Address is required!");
            return;
        }
        if (TextUtils.isEmpty(guideGender)) {
            guideGenderAutoCompleteTextView.setError("Gender is required!");
            return;
        }

        // Show progress dialog and initiate registration process
        progressDialog.show();

        // Register guide with server using Volley
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_REGISTER_GUIDE,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");
                        String message = jsonObject.getString("message");

                        if (error) {
                            Toast.makeText(guideRegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(guideRegistrationActivity.this, "Guide registration successful!", Toast.LENGTH_SHORT).show();
                            navigateToDashboard();
                        }
                    } catch (JSONException e) {
                        Log.e("JSONException", e.getMessage());
                        Toast.makeText(guideRegistrationActivity.this, "Error parsing server response.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("VolleyError", error.toString());
                    Toast.makeText(guideRegistrationActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", guideName);
                params.put("guide_id", guideId);
                params.put("contact_number", guideContact);
                params.put("address", guideAddress);
                params.put("gender", guideGender);
                params.put("email", guideEmail);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void navigateToDashboard() {
        // Navigate to dashboard after successful registration
        Intent intent = new Intent(guideRegistrationActivity.this, MainActivity.class);
        intent.putExtra("userEmail" , guideEmail);
        startActivity(intent);
        finish();
    }

    private void enhancedButtonAnimation(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(300);
        animatorSet.start();
    }
}
