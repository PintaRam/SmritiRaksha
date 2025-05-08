package com.smritiraksha.Patient;
//package com.smritiraksha.Patient.Games;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.smritiraksha.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;


public class MedicalReminderFragment extends Fragment {

    private LottieAnimationView lottieAnimationView;
    private TextView tvReminderMessage;
    private TextToSpeech textToSpeech;

    private String patientEmail;

    private Handler handler = new Handler();

    // Current Lottie file and message
    private String currentLottieFile = "clock_animation.json"; // Default animation
    private String currentMessage = "Welcome! Stay tuned for your reminders.";

    private List<Prescription> prescriptions = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve patientEmail from arguments
        if (getArguments() != null) {
            patientEmail = getArguments().getString("patient_email", "");
            Log.d("MedicalReminder", "Received patient email: " + patientEmail);
        } else {
            Log.e("MedicalReminder", "No patient email received");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_reminder, container, false);

        lottieAnimationView = view.findViewById(R.id.lottieAnimationView);
        tvReminderMessage = view.findViewById(R.id.tvReminderMessage);

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.getDefault());
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
        }

        // Start checking for notifications
        startNotificationChecker();

        Button btnViewPrescription = view.findViewById(R.id.btnViewPrescription);
        btnViewPrescription.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MedicationActivity.class);
            intent.putExtra("patientEmail", patientEmail);
            startActivity(intent);
        });


        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Notification permission granted!");
            } else {
                Log.e("Permission", "Notification permission denied.");
            }
        }
    }
    private void startNotificationChecker() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkForNotification();
                handler.postDelayed(this, 1000); // Check every second
            }
        }, 1000);
    }

    private void checkForNotification() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Handle the time from 6 AM to 9 AM with a default animation
        if (hour >= 6 && hour < 9) {
            if (!currentMessage.equals("Good morning! Please wait for your first reminder.")) {
                updateNotification("Good morning! Please wait for your first reminder.", R.raw.clock_animation);
            }
        } else if (hour >= 9 && hour < 10) {
            if (!currentMessage.equals("Take your morning medicine!")) {
                updateNotification("Take your morning medicine!", R.raw.medicine_reminder);
            }
        } else if (hour >= 10 && hour < 13) {
            if (!currentMessage.equals("Drink a glass of water!")) {
                updateNotification("Drink a glass of water!", R.raw.hydration_reminder);
            }
        } else if (hour >= 13 && hour < 17) {
            if (!currentMessage.equals("Time for your lunch!")) {
                updateNotification("Time for your lunch!", R.raw.lunch_reminder);
            }
        } else if (hour >= 17 && hour < 21) {
            if (!currentMessage.equals("Go for your evening walk!")) {
                updateNotification("Go for your evening walk!", R.raw.exercise_reminder);
            }
        } else if (hour >= 21 || hour < 6) { // Bedtime reminder visible until 6 AM
            if (!currentMessage.equals("Prepare for bed and relax!")) {
                updateNotification("Prepare for bed and relax!", R.raw.bedtime_reminder);
            }
        }
    }

    private void updateNotification(String message, int lottieResId) {
        // Update the current message and Lottie resource ID
        currentMessage = message;

        // Set Lottie animation from raw resource
        lottieAnimationView.setAnimation(lottieResId);
        lottieAnimationView.playAnimation();

        // Set reminder message
        tvReminderMessage.setText(message);

        // Speak the message aloud
        if (textToSpeech != null) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
        showNotification(message);
    }

    private void showNotification(String message) {
        Context context = getContext();
        if (context == null) return;

        String channelId = "medical_reminders";  // Unique ID for the channel

        // Create a notification channel (only needed once)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Medical Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Reminds users about their medication and health routine");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Create an Intent to open the app when clicked
        Intent intent = new Intent(context, MainActivity.class); // Change to your main activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.main_logo) // Replace with your app's icon
                .setContentTitle("Medical Reminder")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)  // Dismiss when clicked
                .setContentIntent(pendingIntent);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(1, builder.build());
            }
        } else {
            notificationManager.notify(1, builder.build()); // No permission required for older versions
        }
        // Unique ID for each notification
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}

