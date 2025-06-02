package com.dsa.dsa_spellchecker_project;

public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode current = root;

        char[] charArray = word.toCharArray();
        for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
            char ch = charArray[i];
            current.getChildren().putIfAbsent(ch, new TrieNode());
            current = current.getChildren().get(ch);
        }
        
        current.setEndOfWord(true);
    }

    public boolean search(String word) {
        TrieNode current = root;

        char[] charArray = word.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char ch = charArray[i];
            if (!current.getChildren().containsKey(ch)) {
                return false;
            }
            current = current.getChildren().get(ch);
        }
        
        return current != null && current.isEndOfWord();
    }
}
