package com.smritiraksha.Patient;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smritiraksha.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EmergencyCheckWorker extends Worker {

    private static final String TAG = "EmergencyCheckWorker";
    private static final String PATIENT_EMAIL = "patient1@gmail.com";
    private static final String CHECK_EMERGENCY_URL = Constants.CHECK_EMERGENCY;
    private Context context;

    public EmergencyCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "EmergencyCheckWorker started.");
        checkEmergencyStatus();
        return Result.success();
    }

    private void checkEmergencyStatus() {
        Log.d(TAG, "Making request to: " + CHECK_EMERGENCY_URL);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CHECK_EMERGENCY_URL,
                response -> {
                    Log.d(TAG, "Response: " + response.trim());

                    try {
                        JSONObject jsonResponse = new JSONObject(response.trim());
                        boolean error = jsonResponse.getBoolean("error");

                        if (!error) {
                            String message = jsonResponse.getString("message");

                            if (message.equalsIgnoreCase("Emergency triggered!")) {
                                Log.d(TAG, "Emergency Triggered!");
                                sendEmergencyBroadcast(true); // Trigger alarm
                            } else if (message.equalsIgnoreCase("No emergency alerts.")) {
                                Log.d(TAG, "Emergency Resolved!");
                                sendEmergencyBroadcast(false); // Stop alarm
                            } else {
                                Log.d(TAG, "Unexpected response: " + response.trim());
                            }
                        } else {
                            Log.d(TAG, "API Error: " + jsonResponse.getString("message"));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                    }
                },
                error -> Log.e(TAG, "Volley Error: " + error.getMessage())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("patient_email", PATIENT_EMAIL);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);
    }

    private void sendEmergencyBroadcast(boolean isEmergency) {
        Intent intent = new Intent("com.smritiraksha.EMERGENCY_ALERT");
        intent.putExtra("isEmergency", isEmergency);
        context.sendBroadcast(intent);
        Log.d(TAG, "Broadcast Sent: " + (isEmergency ? "Trigger Alarm" : "Stop Alarm"));
    }

}
