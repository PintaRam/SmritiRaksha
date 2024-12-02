package com.smritiraksha;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class Registration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        MaterialButton registerButton = findViewById(R.id.registerButton);
        TextView loginTextView = findViewById(R.id.loginTextView);

        // Add animation to register button
        registerButton.setOnClickListener(view -> enhancedButtonAnimation(registerButton));

        // Add animation to login text
        loginTextView.setOnClickListener(view -> enhancedButtonAnimation(loginTextView));
    }

    private void enhancedButtonAnimation(View view) {
        // Scale animations
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f, 1f);

        // Fade animations
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.8f, 1f);

        // Translate animation (slight upward motion)
        ObjectAnimator bounce = ObjectAnimator.ofFloat(view, "translationY", 0f, -20f, 10f, 0f);


        // Combine animations
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, fadeOut, bounce);
        animatorSet.setDuration(300);
        animatorSet.start();
    }
}
