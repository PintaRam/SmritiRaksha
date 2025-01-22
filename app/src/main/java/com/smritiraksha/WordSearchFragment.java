package com.smritiraksha;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WordSearchFragment extends Fragment {

    private GridView wordSearchGrid;
    private String selectedWord = "";
    private List<String> wordsToFind; // Track remaining words
    private TextView wordList;
    private List<Character> gridData;

    public WordSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_search, container, false);
        wordSearchGrid = view.findViewById(R.id.word_search_grid);
        wordList = view.findViewById(R.id.word_list);

        Map<String, String> selectedWords = getRandomWords();
        wordsToFind = new ArrayList<>(selectedWords.keySet());

        wordList.setText(String.join("  ", wordsToFind));
        wordSearchGrid.setOnTouchListener(this::onTouch);

        // Generate a grid for the Word Search game
        gridData = generateWordSearchGrid(wordsToFind, 10); // 8x8 grid
        WordSearchAdapter adapter = new WordSearchAdapter(requireContext(), gridData);
        wordSearchGrid.setAdapter(adapter);

        return view;
    }

    private Map<String, String> getRandomWords() {
        Map<String, String> wordMap = new HashMap<>();

        // Add 100 meaningful words with definitions (as before)
        wordMap.put("GALAXY", "A massive system of stars, planets, and space matter bound by gravity.");
        wordMap.put("QUARK", "A fundamental particle and a constituent of matter.");
        wordMap.put("PLATEAU", "A flat, elevated landform that rises sharply above the surrounding area.");
        wordMap.put("BACTERIA", "Microscopic single-celled organisms, some of which cause disease.");
        wordMap.put("TUNDRA", "A cold, treeless biome with frozen subsoil.");
        wordMap.put("ENZYME", "A biological molecule that acts as a catalyst in living organisms.");
        wordMap.put("ISOTOPE", "Atoms of the same element with different numbers of neutrons.");
        wordMap.put("PYRAMID", "A structure with triangular sides converging to a single point.");
        wordMap.put("ECOSYSTEM", "A community of organisms interacting with their environment.");
        wordMap.put("ANTONYM", "A word that has the opposite meaning of another word.");
        wordMap.put("SYMMETRY", "The balanced arrangement of parts on opposite sides of a line.");
        wordMap.put("CIRCUIT", "A closed path through which electric current flows.");
        wordMap.put("EQUINOX", "The time when day and night are of equal length.");
        wordMap.put("CARNIVORE", "An animal that feeds on other animals.");
        wordMap.put("ALGEBRA", "A branch of mathematics dealing with symbols and equations.");
        wordMap.put("FOSSIL", "Preserved remains of ancient organisms.");
        wordMap.put("VOLCANO", "An opening in Earth's crust that ejects lava, ash, and gases.");
        wordMap.put("VACCINE", "A substance used to stimulate the production of antibodies and provide immunity.");
        wordMap.put("PARALLEL", "Lines or planes that never meet.");
        wordMap.put("EQUATION", "A mathematical statement showing equality between expressions.");
        wordMap.put("HORIZON", "The line where the earth or sea appears to meet the sky.");
        wordMap.put("LANTERN", "A portable light source enclosed in a protective covering.");
        wordMap.put("PRISM", "A transparent optical element that refracts light.");
        wordMap.put("NEUTRON", "A subatomic particle with no electric charge.");
        wordMap.put("SPHINX", "A mythical creature with a lion's body and a human head.");
        wordMap.put("PENINSULA", "A landform surrounded by water on three sides.");
        wordMap.put("CHROMOSOME", "A threadlike structure of DNA in the cell nucleus.");
        wordMap.put("FREQUENCY", "The number of occurrences of a repeating event per unit of time.");
        wordMap.put("PROTEIN", "A macromolecule essential for growth and repair in living organisms.");
        wordMap.put("BUNGALOW", "A single-story house.");
        wordMap.put("TSUNAMI", "A large sea wave caused by an underwater earthquake or volcanic eruption.");
        wordMap.put("SYLLABUS", "An outline of topics to be covered in a course.");
        wordMap.put("OXIDATION", "A chemical reaction in which a substance loses electrons.");
        wordMap.put("MEMBRANE", "A thin layer that separates or connects different regions.");
        wordMap.put("CONIFER", "A type of tree that produces cones and needle-like leaves.");
        wordMap.put("PARADOX", "A statement that contradicts itself but may be true.");
        wordMap.put("ARTIFACT", "An object made by humans, often of historical interest.");
        wordMap.put("SYNTAX", "The arrangement of words and phrases to create sentences.");
        wordMap.put("MOSAIC", "A pattern or picture made from small pieces of colored material.");
        wordMap.put("TROPICAL", "Relating to the warm regions near the equator.");
        wordMap.put("CITIZEN", "A legally recognized inhabitant of a state or nation.");
        wordMap.put("METEORITE", "A fragment of rock that survives its journey through Earth's atmosphere.");
        wordMap.put("ANTENNA", "A structure for transmitting or receiving electromagnetic signals.");
        wordMap.put("SEQUENCE", "A specific order in which related events or things follow each other.");
        wordMap.put("HORIZON", "The line where the Earth meets the sky.");
        wordMap.put("MIGRATION", "The movement of animals or people from one region to another.");
        wordMap.put("HABITAT", "The natural home of a plant, animal, or other organism.");
        wordMap.put("SATELLITE", "An object that orbits a planet or a star.");
        wordMap.put("METAPHOR", "A figure of speech comparing two unlike things directly.");
        wordMap.put("ORCHESTRA", "A large group of instrumentalists playing together.");
        wordMap.put("PALETTE", "A thin board used for mixing colors in painting.");
        wordMap.put("BANYAN", "A type of fig tree with aerial roots.");
        wordMap.put("HORIZON", "The apparent line where Earth meets the sky.");
        wordMap.put("TIDAL", "Relating to the rise and fall of sea levels.");
        wordMap.put("QUADRANT", "One of four sections into which a plane is divided.");
        wordMap.put("TORNADO", "A violent rotating column of air extending from a storm to the ground.");
        wordMap.put("PYLON", "A tall structure for supporting cables or lights.");
        wordMap.put("GROTTO", "A small picturesque cave or cavern.");
        wordMap.put("ORBITAL", "Relating to the path of an object around a celestial body.");
        wordMap.put("CANYON", "A deep valley with steep sides, often with a river flowing through it.");
        wordMap.put("NEBULA", "A cloud of gas and dust in outer space.");
        wordMap.put("CIRCUS", "A traveling company of acrobats, clowns, and other performers.");
        wordMap.put("DOMAIN", "An area of knowledge or activity.");

        // Shuffle and select random words
        List<String> wordKeys = new ArrayList<>(wordMap.keySet());
        Collections.shuffle(wordKeys);

        // Ensure there are enough words (select at most 5 words)
        int numWordsToSelect = Math.min(5, wordKeys.size());
        Map<String, String> selectedWords = new HashMap<>();
        for (int i = 0; i < numWordsToSelect; i++) {
            selectedWords.put(wordKeys.get(i), wordMap.get(wordKeys.get(i)));
        }
        return selectedWords;
    }

    private List<Character> generateWordSearchGrid(List<String> wordsToFind, int gridSize) {
        // Make sure the grid size is always a positive number
        if (gridSize <= 0) {
            throw new IllegalArgumentException("Grid size must be positive.");
        }

        // Force grid to be fixed at 8x8
        gridSize = 10;  // If you want a fixed grid of 8x8, set this directly

        // Create the empty grid
        char[][] grid = new char[gridSize][gridSize];
        Random random = new Random();

        // Initialize the grid with empty characters
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                grid[row][col] = '\0'; // Empty spaces are marked as '\0'
            }
        }

        // Place words in the grid
        for (String word : wordsToFind) {
            boolean wordPlaced = false;

            // Ensure the word can fit in the grid
            if (word.length() > gridSize) {
                throw new IllegalArgumentException("Word is too long for the grid.");
            }

            while (!wordPlaced) {
                // Randomly choose a row and column for word placement
                int row = random.nextInt(gridSize); // Ensure gridSize is valid here

                // Ensure there is enough space for the word to fit
                int col = random.nextInt(gridSize - word.length() + 1); // Adjust for word length

                // Try placing the word horizontally
                if (col + word.length() <= gridSize) {
                    boolean canPlace = true;

                    // Check if word can be placed in the chosen location
                    for (int i = 0; i < word.length(); i++) {
                        if (grid[row][col + i] != '\0') {
                            canPlace = false; // Collision detected, can't place the word
                            break;
                        }
                    }

                    if (canPlace) {
                        // Place the word horizontally in the grid
                        for (int i = 0; i < word.length(); i++) {
                            grid[row][col + i] = word.charAt(i);
                        }
                        wordPlaced = true; // Word placed successfully
                    }
                }
            }
        }

        // Fill empty spaces with random letters
        List<Character> gridData = new ArrayList<>();
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col] == '\0') {
                    grid[row][col] = (char) ('A' + random.nextInt(26)); // Fill with random letter
                }
                gridData.add(grid[row][col]);
            }
        }

        return gridData;
    }


    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startSelection(event);
                break;
            case MotionEvent.ACTION_MOVE:
                trackSelection(event);
                break;
            case MotionEvent.ACTION_UP:
                validateSelection();
                break;
        }
        return true;
    }

    private void startSelection(MotionEvent event) {
        int position = wordSearchGrid.pointToPosition((int) event.getX(), (int) event.getY());
        selectedWord = ""; // Clear previously selected word
        char letter = gridData.get(position);
        selectedWord += letter;
        highlightCell(position, Color.YELLOW);
    }

    private void trackSelection(MotionEvent event) {
        int position = wordSearchGrid.pointToPosition((int) event.getX(), (int) event.getY());
        char letter = gridData.get(position);
        selectedWord += letter;
        highlightCell(position, Color.YELLOW);
    }

    private void validateSelection() {
        if (wordsToFind.contains(selectedWord)) {
            highlightWord(selectedWord, Color.GREEN);
            wordsToFind.remove(selectedWord);
        } else {
            highlightWord(selectedWord, Color.RED);
            resetHighlights();
        }
    }

    private void highlightCell(int position, int color) {
        View cell = wordSearchGrid.getChildAt(position);
        if (cell != null) {
            cell.setBackgroundColor(color);
        }
    }

    private void highlightWord(String word, int color) {
        // Here, you'd need a better way to track and highlight the word in gridData
        // As an example, we highlight the first few characters of selected word
        for (int i = 0; i < word.length(); i++) {
            int position = i; // Update position according to your word placement logic
            highlightCell(position, color);
        }
    }

    private void resetHighlights() {
        new Handler().postDelayed(() -> {
            for (int i = 0; i < wordSearchGrid.getChildCount(); i++) {
                wordSearchGrid.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }
        }, 1000);
    }
}
