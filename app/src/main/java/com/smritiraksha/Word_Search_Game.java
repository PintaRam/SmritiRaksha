package com.smritiraksha;


import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class Word_Search_Game extends AppCompatActivity {
    private GridLayout gridLayout;
    private TextView wordListTitle, titleText;
    private List<String> wordsToFind;
    private LinearLayout wordListContainer;
    private String[] grid = new String[25]; // 5x5 grid
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_search_game);
        gridLayout = findViewById(R.id.gridLayout);
        wordListContainer=findViewById(R.id.wordListContainer);
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
            // Add the TextView to the wordListContainer (LinearLayout)
            wordListContainer.addView(textView);
        }

        generateWordSearch();
        // Initialize and populate the grid with random letters and the words to find
       fillGridWithRandomLetters();
    }

    // This function generates the word search grid
    private void generateWordSearch() {
        // Set a 5x5 grid
        for (int i = 0; i < grid.length; i++) {
            grid[i] = " "; // Empty space
        }

        // Optional: You can place words in the grid by following the logic
        // for placing words, like the previous solution.
    }
    private void fillGridWithRandomLetters() {
        Random rand = new Random();

        // Convert the grid dimensions from dp to pixels (Density-independent Pixels)
        int gridWidthPx = (int) (getResources().getDisplayMetrics().density * 318);  // Convert 318dp to pixels
        int gridHeightPx = (int) (getResources().getDisplayMetrics().density * 317); // Convert 317dp to pixels

        // Calculate cell width and height based on grid size and number of rows/columns (6)
        int cellWidth = gridWidthPx / 6;  // 6 columns
        int cellHeight = gridHeightPx / 6;  // 6 rows

        // Ensure the grid has exactly 36 elements (6x6 grid)
        if (grid == null || grid.length != 36) {
            grid = new String[36];
            Arrays.fill(grid, " ");
        }

        // Fill the grid with random letters if they are empty
        for (int i = 0; i < grid.length; i++) {
            if (grid[i].equals(" ")) {
                grid[i] = String.valueOf((char) ('A' + rand.nextInt(26)));  // Random letter from A-Z
            }
        }

        // Clear any existing views in the GridLayout
        gridLayout.removeAllViews();
        gridLayout.setRowCount(6);
        gridLayout.setColumnCount(6);

        // Populate the GridLayout with TextViews for each letter
        for (int i = 0; i < grid.length; i++) {
            TextView letterView = new TextView(this);
            letterView.setText(grid[i]);
            letterView.setTextSize(24);  // Adjust text size as needed
            letterView.setPadding(0, 0, 0, 0);  // No padding to ensure full space usage
            letterView.setGravity(Gravity.CENTER);  // Center the text inside the cell

            // Apply border drawable resource to each letter
            letterView.setBackgroundResource(R.drawable.cell_border);  // Apply the border to the TextView

            int finalI = i;
            letterView.setOnClickListener(v -> onLetterClicked(letterView, finalI));

            // Set the width and height of each cell in the grid
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cellWidth;  // Set the width of each cell
            params.height = cellHeight;  // Set the height of each cell
            params.rowSpec = GridLayout.spec(i / 6, 1);  // Row index (for 6 rows)
            params.columnSpec = GridLayout.spec(i % 6, 1);  // Column index (for 6 columns)
            letterView.setLayoutParams(params);

            gridLayout.addView(letterView);  // Add the TextView to the GridLayout
        }
    }



    // Handle the letter click event (you can add word search functionality here)
    private void onLetterClicked(TextView letterView, int position) {
        letterView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        // Implement your word selection logic here
    }
}