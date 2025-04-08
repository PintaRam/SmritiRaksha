package com.smritiraksha.Patient.Games;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.smritiraksha.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class memoryGameFragment extends Fragment {

    private final int[] images = {
            R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4,
            R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4
    };
    private ImageButton[] buttons;
    private int firstCardIndex = -1, secondCardIndex = -1;
    private boolean isProcessing = false;

    public memoryGameFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_memory_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridLayout gridLayout = view.findViewById(R.id.gridLayout);
        int numCards = images.length;
        buttons = new ImageButton[numCards];

        // Shuffle images
        List<Integer> imageList = Arrays.asList(images[0], images[1], images[2], images[3],
                images[4], images[5], images[6], images[7]);
        Collections.shuffle(imageList);
        for (int i = 0; i < numCards; i++) images[i] = imageList.get(i);

        // Initialize buttons
        for (int i = 0; i < numCards; i++) {
            buttons[i] = new ImageButton(getContext());
            buttons[i].setImageResource(R.drawable.card_back);
            buttons[i].setId(i);
            int finalI = i;
            buttons[i].setOnClickListener(v -> handleCardClick(finalI));
            gridLayout.addView(buttons[i]);
        }
    }

    private void handleCardClick(int index) {
        if (isProcessing || buttons[index].getTag() != null) return;
        buttons[index].setImageResource(images[index]);

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
            buttons[firstCardIndex].setTag("matched");
            buttons[secondCardIndex].setTag("matched");
        } else {
            buttons[firstCardIndex].setImageResource(R.drawable.card_back);
            buttons[secondCardIndex].setImageResource(R.drawable.card_back);
        }
        firstCardIndex = -1;
        secondCardIndex = -1;
        isProcessing = false;

        // Check if all pairs are found
        boolean allMatched = true;
        for (ImageButton button : buttons) {
            if (button.getTag() == null) {
                allMatched = false;
                break;
            }
        }
        if (allMatched) {
            Toast.makeText(getContext(), "Congratulations! You matched all pairs!", Toast.LENGTH_LONG).show();
        }
    }
}
