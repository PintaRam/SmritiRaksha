package com.smritiraksha;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        LottieAnimationView successAnimation = findViewById(R.id.success_animation);
        successAnimation.playAnimation(); // Start the animation

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            // Go back to the Sudoku screen
            Intent intent = new Intent(SuccessActivity.this, sudoku.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
