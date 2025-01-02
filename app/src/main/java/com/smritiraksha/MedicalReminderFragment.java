package com.smritiraksha;

import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Calendar;
import java.util.Locale;

public class MedicalReminderFragment extends Fragment {

    private LottieAnimationView lottieAnimationView;
    private TextView tvReminderMessage;
    private TextToSpeech textToSpeech;

    private Handler handler = new Handler();

    // Current Lottie file and message
    private String currentLottieFile = "default_animation.json"; // Default animation
    private String currentMessage = "Welcome! Stay tuned for your reminders.";

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

        // Start checking for notifications
        startNotificationChecker();

        return view;
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
                updateNotification("Good morning! Please wait for your first reminder.", "clock_animation.json");
            }
        } else {
            // Define your reminders based on time
            if (hour == 9 && minute == 0 && !currentMessage.equals("Take your morning medicine!")) {
                updateNotification("Take your morning medicine!", "medicine_reminder.json");
            } else if (hour == 10 && minute == 30 && !currentMessage.equals("Drink a glass of water!")) {
                updateNotification("Drink a glass of water!", "hydration_reminder.json");
            } else if (hour == 13 && minute == 0 && !currentMessage.equals("Time for your lunch!")) {
                updateNotification("Time for your lunch!", "lunch_reminder.json");
            } else if (hour == 17 && minute == 0 && !currentMessage.equals("Go for your evening walk!")) {
                updateNotification("Go for your evening walk!", "exercise_reminder.json");
            } else if (hour == 21 && minute == 0 && !currentMessage.equals("Prepare for bed and relax!")) {
                updateNotification("Prepare for bed and relax!", "bedtime_reminder.json");
            }
        }

        // The same Lottie animation and message remain visible until the next update
    }

    private void updateNotification(String message, String lottieFile) {
        // Update the current message and Lottie file
        currentMessage = message;
        currentLottieFile = lottieFile;

        // Set Lottie animation
        lottieAnimationView.setAnimation(lottieFile);
        lottieAnimationView.playAnimation();

        // Set reminder message
        tvReminderMessage.setText(message);

        // Speak the message aloud
        if (textToSpeech != null) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
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

