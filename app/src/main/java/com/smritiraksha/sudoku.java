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

import java.util.Random;

public class sudoku extends AppCompatActivity {

    private GridLayout sudokuGrid;
    private LottieAnimationView successAnimation;

    private int[][] solutionGrid = new int[9][9];
    private int[][] puzzleGrid = new int[9][9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sudoku);

        sudokuGrid = findViewById(R.id.sudoku_grid);
        successAnimation = findViewById(R.id.success_animation);

        generateNewPuzzle();
        populateGrid();

        findViewById(R.id.reset_button).setOnClickListener(v -> {
            generateNewPuzzle();
            populateGrid();
            if (successAnimation != null) {
                successAnimation.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.check_button).setOnClickListener(v -> checkSolution());
    }

    private void generateNewPuzzle() {
        generateFullSudoku(solutionGrid);
        createPuzzleGrid();
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
        cell.setTag(row + "," + col);

        if (puzzleGrid[row][col] != 0) {
            cell.setText(String.valueOf(puzzleGrid[row][col]));
            cell.setEnabled(false);
        }

        return cell;
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
            Intent intent = new Intent(sudoku.this, SuccessActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Incorrect solution. Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateFullSudoku(int[][] grid) {
        fillGrid(grid);
    }

    private boolean fillGrid(int[][] grid) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (grid[row][col] == 0) {
                    for (int num : getShuffledNumbers()) {
                        if (isValid(grid, row, col, num)) {
                            grid[row][col] = num;
                            if (fillGrid(grid)) {
                                return true;
                            }
                            grid[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private int[] getShuffledNumbers() {
        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Random rand = new Random();
        for (int i = numbers.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            int temp = numbers[i];
            numbers[i] = numbers[index];
            numbers[index] = temp;
        }
        return numbers;
    }

    private boolean isValid(int[][] grid, int row, int col, int num) {
        for (int x = 0; x < 9; x++) {
            if (grid[row][x] == num || grid[x][col] == num) {
                return false;
            }
        }

        int startRow = (row / 3) * 3, startCol = (col / 3) * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[startRow + i][startCol + j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private void createPuzzleGrid() {
        Random rand = new Random();
        int cellsToRemove = 40;

        for (int row = 0; row < 9; row++) {
            System.arraycopy(solutionGrid[row], 0, puzzleGrid[row], 0, 9);
        }

        while (cellsToRemove > 0) {
            int row = rand.nextInt(9);
            int col = rand.nextInt(9);
            if (puzzleGrid[row][col] != 0) {
                puzzleGrid[row][col] = 0;
                cellsToRemove--;
            }
        }
    }
}