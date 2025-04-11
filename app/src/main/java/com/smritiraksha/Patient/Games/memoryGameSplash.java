package com.smritiraksha.Patient.Games;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.smritiraksha.R;

public class memoryGameSplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game_splash);

        Button playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Memory Game Activity
                Intent intent = new Intent(memoryGameSplash.this, Patient_Mem_Game.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
