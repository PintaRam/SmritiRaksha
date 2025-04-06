package com.smritiraksha.Patient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.smritiraksha.R;

public class ProfileFragment extends Fragment {

    private String patientId, patientName, patientContact, patientAge, patientGender, patientEmail, guideId, guideName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView tvPatientId = view.findViewById(R.id.patient_id);
        TextView tvPatientName = view.findViewById(R.id.patient_name);
        TextView tvPatientContact = view.findViewById(R.id.patient_contact);
        TextView tvPatientAge = view.findViewById(R.id.patient_age);
        TextView tvPatientGender = view.findViewById(R.id.patient_gender);
        TextView tvPatientEmail = view.findViewById(R.id.patient_email);
        TextView tvGuideId = view.findViewById(R.id.guide_id);
        TextView tvGuideName = view.findViewById(R.id.guide_name);

        // Populate fields
        if (getArguments() != null) {
            patientId = getArguments().getString("patientId");
            patientName = getArguments().getString("patientName");
            patientContact = getArguments().getString("patientContact");
            patientAge = getArguments().getString("patientAge");
            patientGender = getArguments().getString("patientGender");
            patientEmail = getArguments().getString("patientEmail");
            guideId = getArguments().getString("guideId");
            guideName = getArguments().getString("guideName");

            tvPatientId.setText("Patient ID: " + patientId);
            tvPatientName.setText("Name: " + patientName);
            tvPatientContact.setText("Contact: " + patientContact);
            tvPatientAge.setText("Age: " + patientAge);
            tvPatientGender.setText("Gender: " + patientGender);
            tvPatientEmail.setText("Email: " + patientEmail);
            tvGuideId.setText("Guide ID: " + guideId);
            tvGuideName.setText("Guide Name: " + guideName);
        }

        return view;
    }

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
