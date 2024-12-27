package com.smritiraksha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class patient extends AppCompatActivity {

    private TextInputEditText etPatientName, etPatientID, etContact, etAge;
    private MaterialAutoCompleteTextView actGender, actGuideID, actGuideName;
    private ArrayList<String> guideIDs = new ArrayList<>();
    private ArrayList<String> guideNames = new ArrayList<>();
    private String selectedGuideName;
    private final String email = "pintu.borana20@gmail.com";
    private static final String TAG = "PatientActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient); // Replace with your layout file

        // Initialize UI components
        initializeUI();

        // Populate Gender Dropdown
        populateGenderDropdown();

        // Fetch Guide Details
        fetchGuideDetails();

        // Set Submit Button Listener
        MaterialButton btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> {
            if (validateAndSubmitForm()) {

            }
        });
    }

    private void initializeUI() {
        etPatientName = findViewById(R.id.et_patient_name);
        etPatientID = findViewById(R.id.et_patient_id);
        etContact = findViewById(R.id.et_contact);
        etAge = findViewById(R.id.et_age);
        actGender = findViewById(R.id.act_gender);
        actGuideID = findViewById(R.id.act_guide_id);
        actGuideName = findViewById(R.id.act_guide_name);
    }

    private void populateGenderDropdown() {
        String[] genderOptions = {"Male", "Female", "Other"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, genderOptions);
        actGender.setAdapter(genderAdapter);
        actGender.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) actGender.showDropDown();
        });
    }

    private void fetchGuideDetails() {
        // Fetch Guide Details from the server
        String url = Constants.get_Guide; // Replace with your server URL
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    Log.d("GuideResponse", response); // Log the response for debugging
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        if (!responseObject.getBoolean("error")) {
                            JSONArray guidesArray = responseObject.getJSONArray("guides");
                            guideIDs.clear();
                            guideNames.clear();

                            for (int i = 0; i < guidesArray.length(); i++) {
                                JSONObject guide = guidesArray.getJSONObject(i);
                                guideIDs.add(guide.getString("guide_id"));
                                guideNames.add(guide.getString("name"));
                            }

                            // Set up dropdown for Guide IDs
                            ArrayAdapter<String> guideIDAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, guideIDs);
                            actGuideID.setAdapter(guideIDAdapter);

                            actGuideID.setOnFocusChangeListener((v, hasFocus) -> {
                                if (hasFocus) actGuideID.showDropDown();
                            });
//
//                            // Set up dropdown for Guide Names
//                            ArrayAdapter<String> guideNameAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, guideNames);
//                            actGuideName.setAdapter(guideNameAdapter);
//                            actGuideName.setOnFocusChangeListener((v, hasFocus) -> {
//                                if (hasFocus) actGuideName.showDropDown();
//                            });

                            // Automatically set corresponding Guide Name/ID when one is selected
                            actGuideID.setOnItemClickListener((parent, view, position, id) -> {
                                actGuideName.setText(guideNames.get(position)); // Auto-fill Guide Name based on selected ID
                            });

                            actGuideName.setOnItemClickListener((parent, view, position, id) -> {
                                actGuideID.setText(guideIDs.get(position)); // Auto-fill Guide ID based on selected Name
                            });
                        } else {
                            Toast.makeText(this, "Error fetching guide data", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to parse guide data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("GuideError", "Error fetching guides", error);
                    Toast.makeText(this, "Error fetching guides: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }





    private boolean validateAndSubmitForm() {
        String patientName = etPatientName.getText().toString().trim();
        String patientID = etPatientID.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String gender = actGender.getText().toString().trim();
        String guideID = actGuideID.getText().toString().trim();
        selectedGuideName = actGuideName.getText().toString().toString();

        if (isFieldEmpty(patientName, "Enter patient name", etPatientName) ||
                isFieldEmpty(patientID, "Enter patient ID", etPatientID) ||
                isFieldEmpty(contact, "Enter valid contact", etContact) ||
                isFieldEmpty(age, "Enter a valid age", etAge) ||
                isFieldEmpty(gender, "Select a gender", null) || // Pass null as we don't need to set error for this field
                isFieldEmpty(guideID, "Select a guide ID", null)) { // Pass null for this one too
            return false;
        }


        submitPatientRegistration(patientName, patientID, contact, age, gender, guideID, selectedGuideName);
        return true;
    }

    private boolean isFieldEmpty(String value, String errorMessage, TextInputEditText editText) {
        if (value.isEmpty()) {
            if (editText != null) editText.setError(errorMessage);
            else Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void submitPatientRegistration(String name, String id, String contact, String age, String gender, String guideID, String guideName) {
        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering patient...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_REGISTER_PATIENTS,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");
                        String message = jsonObject.getString("message");

                        if (error) {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Server Response", "Response: " + response);
                            Toast.makeText(this, "Patient registered successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(patient.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        Log.e("JSONException", e.getMessage());
                        Log.e("Server Response", "Response: " + response);

                        Log.e("JSONException1", "ram");
                        Log.e("JSONException1", "ram");
                        Log.e("JSONException1", "ram");
                        Toast.makeText(this, "Error parsing server response.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("VolleyError", error.toString());
                    Toast.makeText(this, "Error registering patient. Please try again.", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("patient_name", name);
                params.put("patient_id", id);
                params.put("contact", contact);
                params.put("email",email);
                params.put("age", age);
                params.put("gender", gender);
                params.put("guide_id", guideID);
                params.put("guide_name", guideName);
                Log.d(TAG, "Request Params: " + params);
                return params;
            }
        };

        // Add request to the queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

}
