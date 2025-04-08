package com.smritiraksha.Patient.Games;

import android.os.Bundle;
import android.os.Handler;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.smritiraksha.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Patient_Mem_Game extends AppCompatActivity {
    private final int[] images = {
            R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4,
            R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4
    };

    private ImageButton[] buttons;
    private int firstCardIndex = -1, secondCardIndex = -1;
    private boolean isProcessing = false;
    private boolean[] matchedCards = new boolean[8]; // Tracks matched cards

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_mem_game);

        GridLayout gridLayout = findViewById(R.id.gridLayout);
        int numCards = images.length;
        buttons = new ImageButton[numCards];

        // Shuffle images randomly
        List<Integer> imageList = Arrays.asList(images[0], images[1], images[2], images[3],
                images[4], images[5], images[6], images[7]);
        Collections.shuffle(imageList);
        for (int i = 0; i < numCards; i++) images[i] = imageList.get(i);

        // Initialize buttons (cards)
        for (int i = 0; i < numCards; i++) {
            buttons[i] = new ImageButton(this);
            buttons[i].setImageResource(R.drawable.card_back); // Initially all cards face down
            buttons[i].setId(i);
            buttons[i].setScaleType(ImageButton.ScaleType.CENTER_CROP);
            buttons[i].setPadding(10, 10, 10, 10);

            int finalI = i;
            buttons[i].setOnClickListener(v -> handleCardClick(finalI));
            gridLayout.addView(buttons[i]);
        }
    }

    private void handleCardClick(int index) {
        if (isProcessing || matchedCards[index]) return; // Ignore clicks on matched cards

        buttons[index].setImageResource(images[index]); // Show the selected card

        if (firstCardIndex == -1) {
            firstCardIndex = index;
        } else {
            secondCardIndex = index;
            isProcessing = true;
            new Handler().postDelayed(this::checkMatch, 1000);
        }
    }

    private void checkMatch() {
        if (images[firstCardIndex] == images[secondCardIndex]) {
            matchedCards[firstCardIndex] = true;
            matchedCards[secondCardIndex] = true;
        } else {
            buttons[firstCardIndex].setImageResource(R.drawable.card_back);
            buttons[secondCardIndex].setImageResource(R.drawable.card_back);
        }

        firstCardIndex = -1;
        secondCardIndex = -1;
        isProcessing = false;

        // Check if all pairs are found
        boolean allMatched = true;
        for (boolean matched : matchedCards) {
            if (!matched) {
                allMatched = false;
                break;
            }
        }
        if (allMatched) {
            Toast.makeText(this, "Congratulations! You matched all pairs!", Toast.LENGTH_LONG).show();
        }
    }
}
