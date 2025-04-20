package com.smritiraksha;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.smritiraksha.CareTaker.guidesMainActivity;
import com.smritiraksha.Doctor.DoctorDashboard;
import com.smritiraksha.Patient.MainActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends AppCompatActivity {

    private MaterialAutoCompleteTextView roleAutoCompleteTextView;
    private TextInputEditText emailEditText, passwordEditText;
    String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        roleAutoCompleteTextView = findViewById(R.id.roleAutoCompleteTextView);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        // Set up the role dropdown
        String[] roles = {"Doctor", "Caretaker", "Patient"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
        roleAutoCompleteTextView.setAdapter(adapter);

        roleAutoCompleteTextView.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                roleAutoCompleteTextView.showDropDown();
            }
        });

        // Register button click listener
        findViewById(R.id.registerButton).setOnClickListener(v -> {
            if (validateFields()) {
                new LoginTask().execute(
                        emailEditText.getText().toString().trim(),
                        passwordEditText.getText().toString().trim(),
                        roleAutoCompleteTextView.getText().toString().trim()  // Pass the role
                );
            }
        });

        // Login TextView (used to redirect to the Registration activity)
        findViewById(R.id.loginTextView).setOnClickListener(v -> {
            Toast.makeText(Login.this, "Redirecting to Register...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // Validate the fields before making a network request
    private boolean validateFields() {
        // Check Role
        String role = roleAutoCompleteTextView.getText().toString().trim();
        if (TextUtils.isEmpty(role)) {
            roleAutoCompleteTextView.setError("Please select a role.");
            return false;
        }

        // Check Email
        email = emailEditText.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email address.");
            return false;
        }

        // Check Password
        String password = passwordEditText.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required.");
            return false;
        } else if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters long.");
            return false;
        }

        return true;
    }

    // AsyncTask to handle login request to the server
    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];
            String role = params[2];

            try {
                // Set up the connection
                URL url = new URL(Constants.login_url); // URL from Constants class
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Create the POST data including the role
                String postData = "email=" + email + "&password=" + password + "&role=" + role;

                // Send data to the server
                OutputStream os = connection.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                // Read the response from the server
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (result != null) {
                    // Parse the JSON response from the server
                    JSONObject jsonResponse = new JSONObject(result);
                    boolean error = jsonResponse.getBoolean("error");

                    if (!error) {
                        // Successful login, redirect to role-specific activity
                        String userId = jsonResponse.getString("user_id");
                        String role = roleAutoCompleteTextView.getText().toString();

                        Toast.makeText(Login.this, "Login Successful as " + role, Toast.LENGTH_SHORT).show();

                        Intent intent;
                        switch (role) {
                            case "Doctor":
                                intent = new Intent(Login.this, DoctorDashboard.class);
                                break;
                            case "Caretaker":
                                intent = new Intent(Login.this, guidesMainActivity.class);
                                break;
                            case "Patient":
                                intent = new Intent(Login.this, MainActivity.class);
                                break;
                            default:
                                Toast.makeText(Login.this, "Invalid role. Please try again.", Toast.LENGTH_SHORT).show();
                                return;
                        }

                        intent.putExtra("userEmail", email);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                        finish(); // Close the login activity
                    } else {
                        String errorMessage = jsonResponse.getString("message");
                        Toast.makeText(Login.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "Network error, please try again later.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(Login.this, "Error processing login response. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
