package com.smritiraksha;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class emergency_guide extends Fragment {

    private MaterialButton btnEmergencyCall, btnTurnOffEmergency;
    private static final String PATIENT_EMAIL = "patient1@gmail.com"; // Patient Email
    private static final String EMERGENCY_API_URL = Constants.TRIGGER_EMERGENCY;

    private boolean isEmergencyTriggered = false; // To track whether emergency has been triggered

    public emergency_guide() {
        // Required empty public constructor
    }

    public static emergency_guide newInstance() {
        return new emergency_guide();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_emergency_guide, container, false);
        btnEmergencyCall = rootView.findViewById(R.id.btn_emergency_call);
        btnTurnOffEmergency = rootView.findViewById(R.id.btn_turn_off_emergency);

        // Ensure the buttons are enabled and visible
        btnEmergencyCall.setEnabled(true);
        btnTurnOffEmergency.setEnabled(true);

        // Set OnClickListener for SOS Button (Emergency Call)
        btnEmergencyCall.setOnClickListener(view -> {
            Log.d("EmergencyAlert", "SOS Button Clicked");  // Add Log here to confirm button click
            if (!isEmergencyTriggered) { // Only trigger if not already triggered
                Log.d("EmergencyAlert", "Triggering Emergency");  // Log for debugging
                sendEmergencyAlert("trigger");
                isEmergencyTriggered = true; // Mark emergency as triggered
            } else {
                Toast.makeText(getContext(), "Emergency already triggered!", Toast.LENGTH_SHORT).show();
            }
        });


        // Set OnClickListener for Turn Off Emergency Button
        btnTurnOffEmergency.setOnClickListener(view -> {
            Log.d("EmergencyAlert", "Turn Off Button Clicked");
            sendEmergencyAlert("reset");
            btnEmergencyCall.setText("SOS");
            isEmergencyTriggered = false; // Reset emergency state when turn off button is clicked
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reset the button state when the fragment is resumed to ensure it works after switching
        if (btnEmergencyCall != null) {
            btnEmergencyCall.setEnabled(true);
            btnEmergencyCall.setText("SOS");
            isEmergencyTriggered = false; // Reset the emergency state when the fragment is resumed
        }
    }

    /**
     * Sends an emergency alert by making a network request to the backend.
     *
     * @param action The action to be triggered ("trigger" or "reset").
     */
    private void sendEmergencyAlert(String action) {
        Log.d("EmergencyAlert", "Sending Request with Action: " + action);  // Debugging log

        // Disable the SOS button temporarily to prevent multiple triggers
        if (action.equals("trigger")) {
            btnEmergencyCall.setEnabled(false);  // Disable SOS button after it is clicked
            btnEmergencyCall.setText("Triggered..."); // Change the button text as feedback to user
        }

        // Create a POST request to trigger the emergency alert
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EMERGENCY_API_URL,
                response -> {
                    Log.d("EmergencyAlert", "Response: " + response);  // Log the response for debugging

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean error = jsonResponse.getBoolean("error");

                        if (!error) {
                            // If no error, show success message
                            Toast.makeText(getContext(), "Emergency " + action + "ed!", Toast.LENGTH_SHORT).show();

                            // Re-enable SOS button if the action is "trigger"
                            if (action.equals("trigger")) {
                                btnEmergencyCall.setEnabled(false);
                                btnEmergencyCall.setText("Triggered...");
                            }

                            // If reset action, enable the button and reset text
                            if (action.equals("reset")) {
                                btnEmergencyCall.setText("SOS");
                                btnEmergencyCall.setEnabled(true);
                            }

                        } else {
                            // Handle error case
                            Toast.makeText(getContext(), "Error sending emergency alert", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }

                },
                error -> {
                    Log.e("EmergencyAlert", "Error: " + error.getMessage());  // Log the error for debugging
                    Toast.makeText(getContext(), "Error sending emergency alert", Toast.LENGTH_SHORT).show();

                    // Ensure button is re-enabled on error as well
                    if (action.equals("trigger")) {
                        btnEmergencyCall.setEnabled(true);
                        btnEmergencyCall.setText("SOS");
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                // Set the parameters to send in the request
                Map<String, String> params = new HashMap<>();
                params.put("patient_email", PATIENT_EMAIL);
                params.put("action", action);  // Action is either "trigger" or "reset"
                return params;
            }
        };

        // Add the request to the request queue to execute it
        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }

}
