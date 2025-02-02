package com.smritiraksha;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import android.content.Intent;
import android.net.Uri;

public class emergency_guide extends Fragment {

    private MaterialButton btnEmergencyCall;
    private MediaPlayer mediaPlayer;

    public emergency_guide() {
        // Required empty public constructor
    }

    public static emergency_guide newInstance() {
        return new emergency_guide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_emergency_guide, container, false);

        // Initialize button
        btnEmergencyCall = rootView.findViewById(R.id.btn_emergency_call);

        // Emergency Button Click
        btnEmergencyCall.setOnClickListener(view -> {
            playSosSound();
            callForHelp();
        });

        return rootView;
    }

    // Play SOS Sound
    private void playSosSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getContext(), R.raw.sos_sound);
        }
        mediaPlayer.start();
    }

    // Trigger Emergency Call
    private void callForHelp() {
        String emergencyNumber = "911"; // Replace with your emergency contact number
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + emergencyNumber));
        startActivity(callIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
