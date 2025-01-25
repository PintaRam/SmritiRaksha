package com.smritiraksha;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class sudoku extends AppCompatActivity {

    private GridLayout sudokuGrid;
    private LottieAnimationView successAnimation;

    // Solution grid and initial puzzle
    private final int[][] solutionGrid = {
            {5, 3, 4, 6, 7, 8, 9, 1, 2},
            {6, 7, 2, 1, 9, 5, 3, 4, 8},
            {1, 9, 8, 3, 4, 2, 5, 6, 7},
            {8, 5, 9, 7, 6, 1, 4, 2, 3},
            {4, 2, 6, 8, 5, 3, 7, 9, 1},
            {7, 1, 3, 9, 2, 4, 8, 5, 6},
            {9, 6, 1, 5, 3, 7, 2, 8, 4},
            {2, 8, 7, 4, 1, 9, 6, 3, 5},
            {3, 4, 5, 2, 8, 6, 1, 7, 9}
    };

    private final int[][] puzzleGrid = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 7, 0, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sudoku);

        sudokuGrid = findViewById(R.id.sudoku_grid);
        successAnimation = findViewById(R.id.success_animation);

        // Populate the grid with the puzzle
        populateGrid();

        // Reset button functionality
        findViewById(R.id.reset_button).setOnClickListener(v -> resetGrid());

        // Check button functionality
        findViewById(R.id.check_button).setOnClickListener(v -> checkSolution());
    }

    private void populateGrid() {
        sudokuGrid.removeAllViews();
        sudokuGrid.setRowCount(9);
        sudokuGrid.setColumnCount(9);

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                EditText cell = createCell(row, col);
                sudokuGrid.addView(cell);
            }
        }
    }

    private EditText createCell(int row, int col) {
        EditText cell = new EditText(this);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(row, 1, 1f);
        params.columnSpec = GridLayout.spec(col, 1, 1f);
        params.setMargins(2, 2, 2, 2);
        cell.setLayoutParams(params);

        cell.setGravity(Gravity.CENTER);
        cell.setInputType(InputType.TYPE_CLASS_NUMBER);
        cell.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
        cell.setTextSize(18);
        cell.setBackgroundResource(R.drawable.cell_border);
        cell.setTag(row + "," + col); // Identify cell by position

        if (puzzleGrid[row][col] != 0) {
            cell.setText(String.valueOf(puzzleGrid[row][col]));
            cell.setEnabled(false);
        }

        return cell;
    }

    private void resetGrid() {
        populateGrid();
        if (successAnimation != null) {
            successAnimation.setVisibility(View.GONE);
        }
    }

    private void checkSolution() {
        boolean isCorrect = true;

        for (int i = 0; i < sudokuGrid.getChildCount(); i++) {
            View view = sudokuGrid.getChildAt(i);
            if (view instanceof EditText) {
                EditText cell = (EditText) view;
                String tag = (String) cell.getTag();
                int row = Integer.parseInt(tag.split(",")[0]);
                int col = Integer.parseInt(tag.split(",")[1]);

                String value = cell.getText().toString();
                int enteredValue = value.isEmpty() ? 0 : Integer.parseInt(value);

                if (enteredValue != solutionGrid[row][col]) {
                    isCorrect = false;
                    cell.setError("Wrong!");
                    cell.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    cell.setError(null);
                    cell.setTextColor(getResources().getColor(android.R.color.black));
                }
            }
        }

        if (isCorrect) {
            // Navigate to SuccessActivity
            Intent intent = new Intent(sudoku.this, SuccessActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Incorrect solution. Try again.", Toast.LENGTH_SHORT).show();
        }
    }

}

