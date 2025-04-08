package com.smritiraksha.Patient.Games;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.smritiraksha.Patient.MainActivity;
import com.smritiraksha.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LogicPuzzleActivity extends AppCompatActivity {

    private static final int MAX_TILES = 5; // Maximum blinking tiles per round
    private static final int[] COLORS = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW}; // Array of colors
    private List<int[]> blinkSequence = new ArrayList<>();
    private Map<String, Integer> userSelections = new HashMap<>();
    private Button[][] colorButtons;
    private Handler handler = new Handler();
    private Random random = new Random();

    private TextView tvInstruction;
    private Button btnStartGame, btnBack;

    private int gridSize = 4; // Grid size
    private boolean userInputEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logic_puzzle);

        tvInstruction = findViewById(R.id.tvInstruction);
        btnStartGame = findViewById(R.id.btnStartGame);
        btnBack = findViewById(R.id.btnBack);
        GridLayout colorGrid = findViewById(R.id.colorGrid);

        initializeGrid(colorGrid);

        btnStartGame.setOnClickListener(v -> startGame());

        // Navigate back to MainActivity
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(LogicPuzzleActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Optional: Call finish to remove this activity from the back stack
        });
    }

    private void initializeGrid(GridLayout gridLayout) {
        colorButtons = new Button[gridSize][gridSize];
        int cellSize = getResources().getDisplayMetrics().widthPixels / (gridSize + 1);

        gridLayout.setColumnCount(gridSize);

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Button button = new Button(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                params.setMargins(6, 6, 6, 6);
                button.setLayoutParams(params);

                button.setBackgroundColor(Color.LTGRAY);
                int finalRow = row;
                int finalCol = col;

                button.setOnClickListener(v -> {
                    if (userInputEnabled) {
                        int currentColor = COLORS[random.nextInt(COLORS.length)];
                        button.setBackgroundColor(currentColor);
                        userSelections.put(finalRow + ":" + finalCol, currentColor);
                        checkUserSelection();
                    }
                });

                colorButtons[row][col] = button;
                gridLayout.addView(button);
            }
        }
    }

    private void startGame() {
        userSelections.clear();
        blinkSequence.clear();

        generateBlinkSequence();
        showBlinkSequence();
    }

    private void generateBlinkSequence() {
        int blinkCount = random.nextInt(MAX_TILES) + 1;
        for (int i = 0; i < blinkCount; i++) {
            int row = random.nextInt(gridSize);
            int col = random.nextInt(gridSize);
            int color = COLORS[random.nextInt(COLORS.length)];
            blinkSequence.add(new int[]{row, col, color});
        }
    }

    private void showBlinkSequence() {
        tvInstruction.setText("Watch the tiles carefully!");
        tvInstruction.setTextSize(18f);
        tvInstruction.setGravity(Gravity.CENTER);
        btnStartGame.setVisibility(View.GONE);

        int delay = 0;
        for (int[] tile : blinkSequence) {
            int row = tile[0];
            int col = tile[1];
            int color = tile[2];
            handler.postDelayed(() -> highlightTile(row, col, color), delay);
            delay += 1000; // Blink each tile with a 1-second interval
        }

        handler.postDelayed(() -> {
            tvInstruction.setText("Now repeat the sequence!");
            tvInstruction.setTextSize(20f);
            tvInstruction.setGravity(Gravity.CENTER);
            userInputEnabled = true; // Enable user interaction
        }, delay);
    }

    private void highlightTile(int row, int col, int color) {
        Button button = colorButtons[row][col];
        button.setBackgroundColor(color); // Highlight
        handler.postDelayed(() -> button.setBackgroundColor(Color.LTGRAY), 500); // Reset after 0.5 seconds
    }

    private void checkUserSelection() {
        if (userSelections.size() == blinkSequence.size()) {
            userInputEnabled = false;
            boolean correct = true;

            for (int i = 0; i < blinkSequence.size(); i++) {
                int[] expected = blinkSequence.get(i);
                String key = expected[0] + ":" + expected[1];
                int expectedColor = expected[2];

                if (!userSelections.containsKey(key) || userSelections.get(key) != expectedColor) {
                    correct = false;
                    break;
                }
            }

            if (correct) {
                tvInstruction.setText("Correct! Moving to the next level.");
                handler.postDelayed(this::startGame, 1000);
            } else {
                tvInstruction.setText("Incorrect. Try again!");
                btnStartGame.setVisibility(View.VISIBLE);
            }
        }
    }
}
