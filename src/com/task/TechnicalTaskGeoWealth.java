package com.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Find all 9-letter English words that can be reduced 1 letter at a time, such that each intermediate word is also
 * valid, until reaching a single-letter valid word.
 *
 * Example:
 * startling -> starting -> staring -> string -> sting -> sing -> sin -> in -> i
 */
public class TechnicalTaskGeoWealth {
    private static final String WORD_LIST_URL = "https://raw.githubusercontent.com/nikiiv/JavaCodingTestOne/master/scrabble-words.txt";
    private static final int MAX_WORD_LENGTH = 9;
    private static final Set<String> VALID_SINGLE_LETTER_WORDS = Set.of("I", "A"); // The only valid 1-letter words in English
    private static final Map<String, Boolean> CACHE = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Set<String> wordsSet = loadWordsFromUrl();

        long start = System.nanoTime();

        Set<String> validWords = wordsSet
                .stream()
                .filter(word -> word.length() == MAX_WORD_LENGTH)
                .filter(word -> checkWord(word, wordsSet, CACHE))
                .collect(Collectors.toSet());

        long end = System.nanoTime();

        System.out.printf("Total words count: %s%n", validWords.size());
        System.out.printf("Completed the search in %.2f seconds", (end - start) / 1_000_000_000.0);
    }

    /**
     * Load the list of valid words from the online dictionary and add valid single-letter words("A", "I"). Uses
     * try-with-resources to automatically close the BufferedReader.
     */
    private static Set<String> loadWordsFromUrl() throws IOException {
        URL url = new URL(WORD_LIST_URL);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            Set<String> loadedWords = reader.lines().collect(Collectors.toSet());
            loadedWords.addAll(VALID_SINGLE_LETTER_WORDS);
            return loadedWords;
        }
    }

    /**
     * Check whether a word can be fully reduced 1 letter at a time, and each intermediate word must also be a
     * valid word, until it becomes “A” or “I”. At each step, remove 1 letter at a time and check if the resulting
     * word exists in the dictionary. If it does, recurse on it. To avoid rechecking words multiple times, cache
     * previously seen words.
     *
     * @param word => Word we are trying to check
     * @param words => Set of all valid English words
     * @param cache => A HashMap to remember previously computed results
     * @return TRUE if the word can be reduced, otherwise FALSE
     */
    private static boolean checkWord(String word, Set<String> words, Map<String, Boolean> cache) {
        /**
         * Case 1
         * If we're down to just 1 letter, we check whether it's "A" or "I" — because only those are valid one-letter
         * English words
         */
        if (word.length() == 1) {
            return VALID_SINGLE_LETTER_WORDS.contains(word);
        }

        /**
         * Case 2
         * If the word has already been checked before, use the cached result
         */
        if (cache.containsKey(word)) {
            return cache.get(word);
        }

        boolean result = false;
        for (int i = 0; i < word.length(); i++) {
            String partOne = word.substring(0, i);
            String partTwo = word.substring(i + 1);
            String newWord = partOne + partTwo;

            // Only continue if the new word is valid word in the English alphabet
            if (words.contains(newWord)) {
                // Can this word be reduced?
                if (checkWord(newWord, words, cache)) {
                    result = true;
                    break;
                }
            }
        }

        // Store the result if the word has already been checked before, use the cached result
        cache.put(word, result);
        return result;
    }
}
