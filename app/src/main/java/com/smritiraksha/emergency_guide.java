package com.smritiraksha;

import android.media.MediaPlayer;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class emergency_guide extends Fragment {

    private MaterialButton btnEmergencyCall;
    private static final String PATIENT_EMAIL = "patient1@gmail.com"; // Patient Email
    private static final String EMERGENCY_API_URL = Constants.TRIGGER_EMERGENCY;

    public emergency_guide() { }

    public static emergency_guide newInstance() {
        return new emergency_guide();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_emergency_guide, container, false);
        btnEmergencyCall = rootView.findViewById(R.id.btn_emergency_call);

        btnEmergencyCall.setOnClickListener(view -> {
            sendEmergencyAlert("trigger");
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
