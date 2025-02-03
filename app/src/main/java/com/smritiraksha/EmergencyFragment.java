package com.smritiraksha;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmergencyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmergencyFragment extends Fragment {

    private MaterialButton btnEmergencyStop;
    private static final String PATIENT_EMAIL = "patient1@gmail.com"; // Patient Email
    private static final String EMERGENCY_API_URL = Constants.TRIGGER_EMERGENCY;

    public EmergencyFragment() { }

    public static EmergencyFragment newInstance() {
        return new EmergencyFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_emergency, container, false);
        btnEmergencyStop = rootView.findViewById(R.id.emergency_button);

        btnEmergencyStop.setOnClickListener(view -> {
            sendEmergencyAlert("reset");
        });

        return rootView;
    }

    private void sendEmergencyAlert(String action) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EMERGENCY_API_URL,
                response -> Toast.makeText(getContext(), "Emergency " + action + "ed!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(getContext(), "Error sending emergency alert", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("patient_email", PATIENT_EMAIL);
                params.put("action", action);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }
}

