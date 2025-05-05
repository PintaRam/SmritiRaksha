package com.smritiraksha.CareTaker;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textview.MaterialTextView;
import com.smritiraksha.Constants;
import com.smritiraksha.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class guide_profile extends Fragment {

    private MaterialTextView guideIdTextView, guideNameTextView, guideContactTextView,
            guideEmailTextView, guideGenderTextView, guideAddressTextView, patientEmailTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment_guide_profile layout.
        View rootView = inflater.inflate(R.layout.fragment_guide_profile, container, false);

        // Initialize guide-only views
        guideIdTextView = rootView.findViewById(R.id.guide_id);
        guideNameTextView = rootView.findViewById(R.id.guide_name);
        guideContactTextView = rootView.findViewById(R.id.guide_contact);
        guideEmailTextView = rootView.findViewById(R.id.guide_email);
        guideGenderTextView = rootView.findViewById(R.id.guide_gender);
        guideAddressTextView = rootView.findViewById(R.id.guide_address);
        patientEmailTextView = rootView.findViewById(R.id.patinet_Email);

        if (getArguments() != null) {
            String guideEmail = getArguments().getString("CareEmail", "");
            if (!guideEmail.isEmpty()) {
                loadGuideProfile(guideEmail);
            } else {
                Toast.makeText(getContext(), "Guide email is missing", Toast.LENGTH_SHORT).show();
            }
        }
        return rootView;
    }
    private void loadGuideProfile(String guideEmail) {
        String url = Constants.GuideInfo;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("status")) {
                            JSONObject caretaker = jsonObject.getJSONObject("caretaker");

                            guideIdTextView.setText("Guide ID: " + caretaker.optString("guide_id", "N/A"));
                            guideNameTextView.setText("Guide Name: " + caretaker.optString("name", "N/A"));
                            guideContactTextView.setText("Contact: " + caretaker.optString("contact_number", "N/A"));
                            guideEmailTextView.setText("Email: " + caretaker.optString("email", "N/A"));
                            guideGenderTextView.setText("Gender: " + caretaker.optString("gender", "N/A"));
                            guideAddressTextView.setText("Address: " + caretaker.optString("address", "N/A"));
                            patientEmailTextView.setText("Patient Email: " + caretaker.optString("patient", "N/A")); // Assuming 'patient' is email
                        } else {
                            Toast.makeText(getContext(), "Guide not found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("CTemail", guideEmail);
                return params;
            }
        };

        Volley.newRequestQueue(getContext()).add(request);
    }
}
