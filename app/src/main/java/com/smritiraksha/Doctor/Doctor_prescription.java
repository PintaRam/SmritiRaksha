package com.smritiraksha.Doctor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smritiraksha.R;

import java.util.HashMap;
import java.util.Map;

public class Doctor_prescription extends Fragment {

    private EditText etPatientId, etPrescriptionTitle, etPrescriptionDescription;
    private TimePicker timePicker;
    private Button btnSubmitPrescription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.doctor_prescription_fragment, container, false);

        etPatientId = view.findViewById(R.id.etPatientId);
        etPrescriptionTitle = view.findViewById(R.id.etPrescriptionTitle);
        etPrescriptionDescription = view.findViewById(R.id.etPrescriptionDescription);
        timePicker = view.findViewById(R.id.timePicker);
        btnSubmitPrescription = view.findViewById(R.id.btnSubmitPrescription);
        TimePicker timePicker = view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        //timePicker.setIs24HourView(true); // Optional: 24-hour format

        btnSubmitPrescription.setOnClickListener(v -> submitPrescription());

        return view;
    }

    private void submitPrescription() {
        String patientId = etPatientId.getText().toString().trim();
        String title = etPrescriptionTitle.getText().toString().trim();
        String description = etPrescriptionDescription.getText().toString().trim();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        if (patientId.isEmpty() || title.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://yourserver.com/insert_prescription.php"; // Replace with your backend URL

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(getContext(), "Prescription submitted!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("patient_id", patientId);
                params.put("title", title);
                params.put("description", description);
                params.put("hour", String.valueOf(hour));
                params.put("minute", String.valueOf(minute));
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }
}

