package com.smritiraksha.Patient.Games;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.smritiraksha.R;

public class LogicPuzzleSplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logic_puzzle_splash);

        // Adjust system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Handler to move to the next screen after 3 seconds
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(LogicPuzzleSplash.this, LogicPuzzleActivity.class); // Adjust destination activity if needed
            startActivity(intent);
            finish();
        }, 3000); // Splash duration: 3 seconds
    }
}
