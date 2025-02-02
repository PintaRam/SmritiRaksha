package com.smritiraksha;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class EmergencyCheckWorker extends Worker {

    private static final String TAG = "EmergencyCheckWorker";
    private static final String PATIENT_EMAIL = "patient1@gmail.com"; // Patient Email
    private static final String CHECK_EMERGENCY_URL = Constants.CHECK_EMERGENCY;
    private MediaPlayer mediaPlayer;

    public EmergencyCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        checkEmergencyStatus();
        return Result.success();
    }

    private void checkEmergencyStatus() {
        String url = CHECK_EMERGENCY_URL + "?patient_email=" + PATIENT_EMAIL;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    if ("1".equals(response.trim())) {
                        playEmergencyAlarm();
                    } else {
                        stopEmergencyAlarm();
                    }
                },
                error -> Log.e(TAG, "Error checking emergency: " + error.getMessage()));

        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void playEmergencyAlarm() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sos_sound);
            mediaPlayer.setLooping(true);
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopEmergencyAlarm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

