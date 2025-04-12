package com.smritiraksha.Patient.Games;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.smritiraksha.Patient.MainActivity;
import com.smritiraksha.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private ImageView Back;
    private Vibrator vibrator;

    private List<String> words ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_search_game);
        gridLayout = findViewById(R.id.gridLayout);
        wordListContainer = findViewById(R.id.wordListContainer);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        Back=findViewById(R.id.BackImg);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainintent=new Intent(Word_Search_Game.this, MainActivity.class);
                startActivity(mainintent);
            }
        });


        words = new ArrayList<>();

        // Adding words to the list
        words.add("Apple");
        words.add("Beach");
        words.add("Brave");
        words.add("Chair");
        words.add("Cloud");
        words.add("Dance");
        words.add("Dream");
        words.add("Earth");
        words.add("Flame");
        words.add("Glove");
        words.add("Happy");
        words.add("House");
        words.add("Jelly");
        words.add("Light");
        words.add("Magic");
        words.add("Night");
        words.add("Ocean");
        words.add("Peace");
        words.add("Power");
        words.add("Quiet");
        words.add("River");
        words.add("Smile");
        words.add("Spark");
        words.add("Stone");
        words.add("Tiger");
        words.add("Unity");
        words.add("Water");
        words.add("Winds");
        words.add("Zebra");
        words.add("Bright");
        words.add("Butter");
        words.add("Castle");
        words.add("Circle");
        words.add("Desert");
        words.add("Friend");
        words.add("Garden");
        words.add("Golden");
        words.add("Hunter");
        words.add("Island");
        words.add("Laughs");
        words.add("Mirror");
        words.add("Orange");
        words.add("Puzzle");
        words.add("Rocket");
        words.add("Secret");
        words.add("Silver");
        words.add("Travel");
        words.add("Valley");
        words.add("Wisdom");
        words.add("Yellow");

        Collections.shuffle(words);

        wordsToFind = new ArrayList<>(words.subList(0, 5));

        // Loop through the list and create a TextView for each word
        for (String word : wordsToFind) {
            TextView textView = new TextView(this);
            textView.setText(word);
            textView.setTextSize(16); // You can adjust the size as needed
            textView.setPadding(10, 10, 10, 10); // Optional padding

            // Create LayoutParams with margins
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    250,  // Width in pixels (replace with dp conversion if needed)
                    80
            );

            // Set the margins (left, top, right, bottom)
            layoutParams.setMargins(10, 10, 10, 10);  // Adjust these values as needed
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
        int gridWidthPx = (int) (getResources().getDisplayMetrics().density * 328);  // 318dp to pixels
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
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        // Get touch coordinates relative to the GridLayout
        int[] gridLocation = new int[2];
        gridLayout.getLocationOnScreen(gridLocation);

        float x = event.getRawX() - gridLocation[0]; // Adjust for GridLayout's X position
        float y = event.getRawY() - gridLocation[1]; // Adjust for GridLayout's Y position

        int gridSize = (int) Math.sqrt(grid.length); // Assuming square grid
        int cellWidth = gridLayout.getWidth() / gridSize;
        int cellHeight = gridLayout.getHeight() / gridSize;

        int col = (int) (x / cellWidth);
        int row = (int) (y / cellHeight);
        int position = row * gridSize + col;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                selectedLetters.clear();
                addLetterToSelection(position);
                break;

            case MotionEvent.ACTION_MOVE:
                if (isValidGridPosition(position)) {
                    addLetterToSelection(position);
                }
                break;

            case MotionEvent.ACTION_UP:
                validateSelection();
                break;
        }
        return true;
    }


    private boolean isValidGridPosition(int position) {
        return position >= 0 && position < grid.length;
    }

    private void addLetterToSelection(int position) {
        if (!isValidGridPosition(position)) return;

        View letterView = gridLayout.getChildAt(position);

        if (letterView instanceof TextView && !selectedLetters.contains(letterView)) {
            selectedLetters.add((TextView) letterView);
            letterView.setBackgroundResource(R.drawable.selected_cell_border); // Highlight selected cell
        }
    }

    private void validateSelection() {
        String selectedWord = getSelectedWord();

        if (wordsToFind.contains(selectedWord)) {
            // Highlight the word in the grid
            highlightWord(selectedWord);
            // Strike it off in the word list
            strikeWordInWordList(selectedWord);
            // Add to found words list
            foundWords.add(selectedWord);
            checkGameCompletion();
        } else {
            // Reset background for incorrectly selected letters
            for (TextView letterView : selectedLetters) {
                letterView.setBackgroundResource(R.drawable.cell_border);
            }
            // Provide feedback for wrong selection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }
        selectedLetters.clear();
    }

    private String getSelectedWord() {
        StringBuilder word = new StringBuilder();
        for (TextView letterView : selectedLetters) {
            word.append(letterView.getText().toString());
        }
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }

    private void highlightWord(String word) {
        for (TextView letterView : selectedLetters) {
            letterView.setBackgroundResource(R.drawable.selected_cell_border); // Highlight the cells of a correct word
        }
    }

    private void strikeWordInWordList(String word) {
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
            showGameCompletionDialog();
        }
    }

    private void showGameCompletionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Congratulations, you found all the words!")
                .setPositiveButton("Start New Game", (dialog, which) -> restartGame())
                .setNegativeButton("Go Back", (dialog, which) -> {
                    Intent mainintent=new Intent(Word_Search_Game.this,MainActivity.class);
                    startActivity(mainintent);
                    finish();
                })
                .show();
    }

    private void restartGame() {
        // Clear found words and selected letters
        foundWords.clear();
        selectedLetters.clear();

        // Regenerate the word search grid
        generateWordSearch();
        Collections.shuffle(words);
        wordsToFind.clear();
        wordsToFind = new ArrayList<>(words.subList(0, 5));
        fillGridWithWordsAndRandomLetters(wordsToFind);

        // Reset word list display
        wordListContainer.removeAllViews();
        for (String word : wordsToFind) {
            TextView textView = new TextView(this);
            textView.setText(word);
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            // Optional padding and margins
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(20, 20, 20, 20);
            textView.setLayoutParams(layoutParams);

            wordListContainer.addView(textView);
        }

        // Clear the GridLayout for a fresh start
        gridLayout.removeAllViews();
        int gridSize = (int) Math.sqrt(grid.length); // Assuming square grid
        populateGridLayout(gridSize);
    }

}
