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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.smritiraksha.CareTaker.guideRegistrationActivity;
import com.smritiraksha.Patient.patient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText, confirmPasswordEditText;
    private MaterialAutoCompleteTextView roleAutoCompleteTextView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize views
        MaterialButton registerButton = findViewById(R.id.registerButton);
        TextView loginTextView = findViewById(R.id.loginTextView);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        roleAutoCompleteTextView = findViewById(R.id.roleAutoCompleteTextView);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering user...");

        // Populate the role dropdown
        String[] roles = {"Doctor", "Caretaker", "Patient"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
        roleAutoCompleteTextView.setAdapter(adapter);

        roleAutoCompleteTextView.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                roleAutoCompleteTextView.showDropDown();
            }
        });

        // Add animations and actions
        registerButton.setOnClickListener(view -> {
            enhancedButtonAnimation(registerButton);
            registerUser();
        });

        loginTextView.setOnClickListener(view -> {
            enhancedButtonAnimation(loginTextView);
            startActivity(new Intent(Registration.this, Login.class));
        });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String role = roleAutoCompleteTextView.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required!");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required!");
            return;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters!");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError("Please confirm your password!");
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match!");
            return;
        }
        if (TextUtils.isEmpty(role)) {
            roleAutoCompleteTextView.setError("Role is required!");
            return;
        }

        // Proceed with registration
        progressDialog.show();

        // Register user via Volley
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_REGISTER,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");
                        String message = jsonObject.getString("message");

                        if (error) {
                            Toast.makeText(Registration.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Registration.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            navigateToRoleActivity(role, email);
                        }
                    } catch (JSONException e) {
                        Log.e("JSONException", e.getMessage());
                        Toast.makeText(Registration.this, "Error parsing server response.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("VolleyError", error.toString());
                    Toast.makeText(Registration.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                params.put("role", role);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }


    private void navigateToRoleActivity(String role , String email) {
        Intent intent;
        switch (role.toLowerCase()) {
            case "doctor":
                intent = new Intent(Registration.this, Login.class);
                break;
            case "caretaker":
                intent = new Intent(Registration.this, guideRegistrationActivity.class);
                break;
            case "patient":
                intent = new Intent(Registration.this, patient.class);
                break;
            default:
                Toast.makeText(this, "Unknown role. Unable to navigate.", Toast.LENGTH_SHORT).show();
                return;
        }
        intent.putExtra("userEmail", email);
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