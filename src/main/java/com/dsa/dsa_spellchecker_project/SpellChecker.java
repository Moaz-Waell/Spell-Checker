package com.dsa.dsa_spellchecker_project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SpellChecker {
    private Trie dictionaryTrie;
    private List<String> dictionaryWords;

    public SpellChecker(String dictionaryPath) {
        dictionaryTrie = new Trie();
        dictionaryWords = new ArrayList<>();
        loadDictionary(dictionaryPath);
    }


    public void loadDictionary(String dictionaryPath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(dictionaryPath))) {
            System.out.println("Successfully opened dictionary: " + dictionaryPath);
            
            String line;
            while ((line = reader.readLine()) != null) {
                // Normalize: convert to lowercase and trim whitespace
                String cleanWord = line.trim().toLowerCase();
                
                if (!cleanWord.isEmpty()) {
                    dictionaryTrie.insert(cleanWord);
                    dictionaryWords.add(cleanWord);
                }
            }
            
            System.out.println("Loaded " + dictionaryWords.size() + " words into the dictionary for suggestions.");
        } catch (IOException e) {
            System.err.println("Error: Could not open dictionary file at: " + dictionaryPath);
            System.err.println("Please ensure the dictionary file is in the correct location, or provide the correct path.");
            e.printStackTrace();
        }
    }


    public boolean checkWord(String word) {
        String lowerWord = word.toLowerCase();
        // Remove punctuation
        lowerWord = lowerWord.replaceAll("[^a-zA-Z0-9]", "");
        
        if (lowerWord.isEmpty()) {
            return true; // Consider empty string after punctuation removal as correct
        }
        
        return dictionaryTrie.search(lowerWord);
    }

    public List<String> splitText(String text) {
        List<String> words = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();

        char[] charArray = text.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char ch = charArray[i];
            if (Character.isWhitespace(ch) || !Character.isLetterOrDigit(ch)) {
                if (currentWord.length() > 0) {
                    words.add(currentWord.toString());
                    currentWord.setLength(0);
                }
            } else {
                currentWord.append(ch);
            }
        }
        
        if (currentWord.length() > 0) {
            words.add(currentWord.toString());
        }
        
        return words;
    }


    public int levenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        
        // Create a matrix of size (len1+1) x (len2+1)
        int[][] dp = new int[len1 + 1][len2 + 1];
        
        // Initialize the first row and column
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }
        
        // Fill the matrix
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        
        return dp[len1][len2];
    }

    public List<String> suggestCorrections(String misspelledWord, int maxDistance) {
        List<String> suggestions = new ArrayList<>();
        final String lowerMisspelledWord = misspelledWord.toLowerCase();
        
        // Remove punctuation
        final String cleanedWord = lowerMisspelledWord.replaceAll("[^a-zA-Z0-9]", "");
        
        if (cleanedWord.isEmpty()) {
            return suggestions;
        }

        for (int i = 0; i < dictionaryWords.size(); i++) {
            String dictWord = dictionaryWords.get(i);
            int distance = levenshteinDistance(cleanedWord, dictWord);
            if (distance <= maxDistance) {
                suggestions.add(dictWord);
            }
        }

        // Sort suggestions by Levenshtein distance
        Collections.sort(suggestions, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return Integer.compare(
                    levenshteinDistance(cleanedWord, a),
                    levenshteinDistance(cleanedWord, b)
                );
            }
        });
        
        return suggestions;
    }

    public List<String> suggestCorrections(String misspelledWord) {
        return suggestCorrections(misspelledWord, 2);
    }
}
