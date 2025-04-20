package com.smritiraksha.Patient.Games;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.smritiraksha.R;

import java.util.ArrayList;
import java.util.Collections;

public class Patient_Mem_Game extends Activity {

    private ImageButton[] cardButtons = new ImageButton[8];
    private int[] cardImages = new int[8];
    private boolean[] matched = new boolean[8];

    private int firstCardIndex = -1;
    private int secondCardIndex = -1;

    private boolean isBusy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_mem_game);

        initCards();
    }

    private void initCards() {
        // Reference buttons
        for (int i = 0; i < 8; i++) {
            String buttonID = "card_" + i;
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            cardButtons[i] = findViewById(resID);
        }

        // Assign images (pairs)
        ArrayList<Integer> images = new ArrayList<>();
        images.add(R.drawable.image1);
        images.add(R.drawable.image1);
        images.add(R.drawable.image2);
        images.add(R.drawable.image2);
        images.add(R.drawable.image3);
        images.add(R.drawable.image3);
        images.add(R.drawable.image4);
        images.add(R.drawable.image4);
        Collections.shuffle(images);

        // Store shuffled order
        for (int i = 0; i < 8; i++) {
            cardImages[i] = images.get(i);
            matched[i] = false;
            cardButtons[i].setImageResource(R.drawable.card_back);
            int finalI = i;
            cardButtons[i].setOnClickListener(v -> onCardClick(finalI));
        }
    }

    public void onCardClick(int index) {
        if (isBusy || matched[index]) return;

        // Flip the card
        cardButtons[index].setImageResource(cardImages[index]);

        if (firstCardIndex == -1) {
            firstCardIndex = index;
        } else if (secondCardIndex == -1 && index != firstCardIndex) {
            secondCardIndex = index;
            isBusy = true;

            new Handler().postDelayed(() -> checkMatch(), 1000);
        }
    }

    private void checkMatch() {
        if (cardImages[firstCardIndex] == cardImages[secondCardIndex]) {
            matched[firstCardIndex] = true;
            matched[secondCardIndex] = true;
            Toast.makeText(this, "Matched!", Toast.LENGTH_SHORT).show();
        } else {
            cardButtons[firstCardIndex].setImageResource(R.drawable.card_back);
            cardButtons[secondCardIndex].setImageResource(R.drawable.card_back);
        }

        firstCardIndex = -1;
        secondCardIndex = -1;
        isBusy = false;

        checkGameEnd();
    }

    private void checkGameEnd() {
        for (boolean m : matched) {
            if (!m) return;
        }
        Toast.makeText(this, "You completed the game!", Toast.LENGTH_LONG).show();
    }

    public void restartGame(View view) {
        firstCardIndex = -1;
        secondCardIndex = -1;
        isBusy = false;
        initCards();
    }

    public void exitGame(View view) {
        finish();
    }
}

