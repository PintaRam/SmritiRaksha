package com.smritiraksha;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

public class guide_profile extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment_guide_profile layout.
        View rootView = inflater.inflate(R.layout.fragment_guide_profile, container, false);

        // Get the data passed as arguments.
        if (getArguments() != null) {
            String guideId = getArguments().getString("guideId", "No Guide ID");
            String guideName = getArguments().getString("guideName", "No Name");
            String guideContact = getArguments().getString("guideContact", "No Contact");
            String patientName = getArguments().getString("patientName", "No Patient Name");
            String patientId = getArguments().getString("patientId", "No Patient ID");
            String contact = getArguments().getString("contact", "No Contact");
            String age = getArguments().getString("age", "No Age");
            String gender = getArguments().getString("gender", "No Gender");
            String email = getArguments().getString("email", "No Email");

            // Log the guide details for debugging.
            Log.d("GuideProfile", "Guide ID: " + guideId);  // Print the guide ID in log.

            // Display the guide and patient information in MaterialCardView's TextViews.
            MaterialTextView guideIdTextView = rootView.findViewById(R.id.guide_id); // Add a TextView to display guide ID
            MaterialTextView guideNameTextView = rootView.findViewById(R.id.guide_name);
            MaterialTextView guideContactTextView = rootView.findViewById(R.id.guide_contact);
            MaterialTextView patientNameTextView = rootView.findViewById(R.id.patient_name);
            MaterialTextView patientIdTextView = rootView.findViewById(R.id.patient_id);
            MaterialTextView patientContactTextView = rootView.findViewById(R.id.patient_contact);
            MaterialTextView patientAgeTextView = rootView.findViewById(R.id.patient_age);
            MaterialTextView patientGenderTextView = rootView.findViewById(R.id.patient_gender);
            MaterialTextView patientEmailTextView = rootView.findViewById(R.id.patient_email);

            // Setting the TextViews with the passed values.
            guideIdTextView.setText("Guide ID: " + guideId);  // Display Guide ID
            guideNameTextView.setText("Guide: " + guideName);
            guideContactTextView.setText("Guide Contact: " + guideContact);
            patientNameTextView.setText("Patient: " + patientName);
            patientIdTextView.setText("Patient ID: " + patientId);
            patientContactTextView.setText("Patient Contact: " + contact);
            patientAgeTextView.setText("Age: " + age);
            patientGenderTextView.setText("Gender: " + gender);
            patientEmailTextView.setText("Email: " + email);
        }

        return rootView;
    }
}
