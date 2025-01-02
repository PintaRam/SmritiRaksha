package com.smritiraksha;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private String patientId, patientName, patientContact, patientAge, patientGender, patientEmail, guideId, guideName;

    // Constructor with data parameters (Constructor-based Approach)
    public ProfileFragment(String patientId, String patientName, String patientContact,
                           String patientAge, String patientGender, String patientEmail,
                           String guideId, String guideName) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientContact = patientContact;
        this.patientAge = patientAge;
        this.patientGender = patientGender;
        this.patientEmail = patientEmail;
        this.guideId = guideId;
        this.guideName = guideName;
    }

    // Default constructor for framework use (Bundle fallback for lifecycle scenarios)
    public ProfileFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fallback to arguments from a Bundle if the data was passed in this way
        if (savedInstanceState == null && getArguments() != null) {
            this.patientId = getArguments().getString("patientId");
            this.patientName = getArguments().getString("patientName");
            this.patientContact = getArguments().getString("patientContact");
            this.patientAge = getArguments().getString("patientAge");
            this.patientGender = getArguments().getString("patientGender");
            this.patientEmail = getArguments().getString("patientEmail");
            this.guideId = getArguments().getString("guideId");
            this.guideName = getArguments().getString("guideName");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize TextViews
        TextView tvPatientId = view.findViewById(R.id.patient_id);
        TextView tvPatientName = view.findViewById(R.id.patient_name);
        TextView tvPatientContact = view.findViewById(R.id.patient_contact);
        TextView tvPatientAge = view.findViewById(R.id.patient_age);
        TextView tvPatientGender = view.findViewById(R.id.patient_gender);
        TextView tvPatientEmail = view.findViewById(R.id.patient_email);
        TextView tvGuideId = view.findViewById(R.id.guide_id);
        TextView tvGuideName = view.findViewById(R.id.guide_name);

        // Set data to TextViews
        tvPatientId.setText("Patient ID: " + patientId);
        tvPatientName.setText("Name: " + patientName);
        tvPatientContact.setText("Contact: " + patientContact);
        tvPatientAge.setText("Age: " + patientAge);
        tvPatientGender.setText("Gender: " + patientGender);
        tvPatientEmail.setText("Email: " + patientEmail);
        tvGuideId.setText("Guide ID: " + guideId);
        tvGuideName.setText("Guide Name: " + guideName);

        return view;
    }

    // Helper to create a new instance using a Bundle
    public static ProfileFragment newInstance(String patientId, String patientName, String patientContact,
                                              String patientAge, String patientGender, String patientEmail,
                                              String guideId, String guideName) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("patientId", patientId);
        args.putString("patientName", patientName);
        args.putString("patientContact", patientContact);
        args.putString("patientAge", patientAge);
        args.putString("patientGender", patientGender);
        args.putString("patientEmail", patientEmail);
        args.putString("guideId", guideId);
        args.putString("guideName", guideName);
        fragment.setArguments(args);
        return fragment;
    }
}
