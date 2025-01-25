package com.smritiraksha;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Word_Search_Game extends AppCompatActivity {
    private GridLayout gridLayout;
    private TextView wordListTitle, titleText;
    private List<String> wordsToFind;
    private GridLayout wordListContainer;
    private String[] grid = new String[36]; // 6x6 grid
    private List<TextView> selectedLetters = new ArrayList<>();
    private List<String> foundWords = new ArrayList<>();
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_search_game);
        gridLayout = findViewById(R.id.gridLayout);
        wordListContainer = findViewById(R.id.wordListContainer);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        wordsToFind = new ArrayList<>(Arrays.asList("WORD", "FIND", "GAME", "JAVA", "ANDROID"));

        // Loop through the list and create a TextView for each word
        for (String word : wordsToFind) {
            TextView textView = new TextView(this);
            textView.setText(word);
            textView.setTextSize(18); // You can adjust the size as needed
            textView.setPadding(10, 10, 10, 10); // Optional padding

            // Create LayoutParams with margins
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            // Set the margins (left, top, right, bottom)
            layoutParams.setMargins(20, 20, 20, 20);  // Adjust these values as needed
            textView.setLayoutParams(layoutParams);
            wordListContainer.addView(textView);
        }

        generateWordSearch();
        fillGridWithWordsAndRandomLetters(wordsToFind);
    }

    // This function generates the word search grid
    private void generateWordSearch() {
        // Set a 6x6 grid
        for (int i = 0; i < grid.length; i++) {
            grid[i] = " "; // Empty space
        }
    }

    private void fillGridWithWordsAndRandomLetters(List<String> wordsToFind) {
        Random rand = new Random();
        int gridSize = 10;
        grid = new String[gridSize * gridSize];
        Arrays.fill(grid, " ");

        // Attempt to place each word in the grid
        for (String word : wordsToFind) {
            boolean placed = false;

            for (int attempts = 0; attempts < 100 && !placed; attempts++) {  // Limit attempts to prevent infinite loops
                int startX = rand.nextInt(gridSize);
                int startY = rand.nextInt(gridSize);
                int direction = rand.nextInt(3);  // 0 = horizontal, 1 = vertical, 2 = diagonal

                placed = placeWordInGrid(word.toUpperCase(), startX, startY, direction, gridSize);
            }

            if (!placed) {
                Log.d("WordSearch", "Unable to place word: " + word);
            }
        }

        // Fill remaining empty cells with random letters
        for (int i = 0; i < grid.length; i++) {
            if (grid[i].equals(" ")) {
                grid[i] = String.valueOf((char) ('A' + rand.nextInt(26)));  // Random letter from A-Z
            }
        }

        // Populate GridLayout
        populateGridLayout(gridSize);
    }

    private boolean placeWordInGrid(String word, int startX, int startY, int direction, int gridSize) {
        int dx = 0, dy = 0;

        switch (direction) {
            case 0: // Horizontal
                dx = 1;
                break;
            case 1: // Vertical
                dy = 1;
                break;
            case 2: // Diagonal
                dx = 1;
                dy = 1;
                break;
        }

        int x = startX, y = startY;
        for (char c : word.toCharArray()) {
            if (x >= gridSize || y >= gridSize || (!grid[y * gridSize + x].equals(" ") && !grid[y * gridSize + x].equals(String.valueOf(c)))) {
                return false;  // Word doesn't fit or conflicts with an existing letter
            }
            x += dx;
            y += dy;
        }

        x = startX;
        y = startY;
        for (char c : word.toCharArray()) {
            grid[y * gridSize + x] = String.valueOf(c);
            x += dx;
            y += dy;
        }

        return true;
    }

    private void populateGridLayout(int gridSize) {
        // Convert grid dimensions from dp to pixels
        int gridWidthPx = (int) (getResources().getDisplayMetrics().density * 322);  // 318dp to pixels
        int gridHeightPx = (int) (getResources().getDisplayMetrics().density * 400); // 317dp to pixels

        int cellWidth = gridWidthPx / gridSize;  // Cell width
        int cellHeight = gridHeightPx / gridSize;  // Cell height

        gridLayout.removeAllViews();
        gridLayout.setRowCount(gridSize);
        gridLayout.setColumnCount(gridSize);

        for (int i = 0; i < grid.length; i++) {
            TextView letterView = new TextView(this);
            letterView.setText(grid[i]);
            letterView.setTextSize(20);
            letterView.setGravity(Gravity.CENTER);
            letterView.setBackgroundResource(R.drawable.cell_border);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cellWidth;
            params.height = cellHeight;
            params.rowSpec = GridLayout.spec(i / gridSize, 1);
            params.columnSpec = GridLayout.spec(i % gridSize, 1);
            letterView.setLayoutParams(params);

            gridLayout.addView(letterView);
        }
    }

    // Handle the letter click event
    private void onLetterClicked(TextView letterView, int position) {
        if (selectedLetters.contains(letterView)) {
            // If already selected, remove from list and reset background
            selectedLetters.remove(letterView);
            letterView.setBackgroundResource(R.drawable.cell_border);
        } else {
            // Add to selected list
            selectedLetters.add(letterView);
            letterView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));  // Highlight selected letter
        }

        // Check if the selected letters form a valid word
        String word = getSelectedWord();
        if (wordsToFind.contains(word)) {
            // Highlight the word in the grid
            highlightWord(word);
            // Strike it off in the word list
            strikeWordInWordList(word);
            // Add to found words list
            foundWords.add(word);
            checkGameCompletion();
        } else if (selectedLetters.size() > word.length()) {
            // If it's a wrong selection, vibrate for feedback
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }
    }

    private void highlightWord(String word) {
        // Highlight word in the grid (set border or background to indicate correct word)
        // Logic for highlighting correct word in the grid
    }

    private void strikeWordInWordList(String word) {
        // Strike through the word in the word list
        for (int i = 0; i < wordListContainer.getChildCount(); i++) {
            TextView wordTextView = (TextView) wordListContainer.getChildAt(i);
            if (wordTextView.getText().toString().equals(word)) {
                wordTextView.setPaintFlags(wordTextView.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                break;
            }
        }
    }

    private void checkGameCompletion() {
        if (foundWords.size() == wordsToFind.size()) {
            // Show dialog when game is complete
            showGameCompletionDialog();
        }
    }

    private void showGameCompletionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Congratulations, you found all the words!")
                .setPositiveButton("Start New Game", (dialog, which) -> restartGame())
                .setNegativeButton("Go Back", (dialog, which) -> finish())
                .show();
    }

    private void restartGame() {
        // Logic to reset the game
        foundWords.clear();
        selectedLetters.clear();
        generateWordSearch();
        fillGridWithWordsAndRandomLetters(wordsToFind);
        wordListContainer.removeAllViews();
        for (String word : wordsToFind) {
            TextView textView = new TextView(this);
            textView.setText(word);
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            wordListContainer.addView(textView);
        }
    }

    // Get the selected word from the list of selected letters
    private String getSelectedWord() {
        StringBuilder word = new StringBuilder();
        for (TextView letterView : selectedLetters) {
            word.append(letterView.getText().toString());
        }
        return word.toString();
    }
}
