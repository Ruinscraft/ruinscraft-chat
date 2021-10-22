package com.ruinscraft.chat.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

// https://gist.github.com/PimDeWitte/c04cc17bc5fa9d7e3aee6670d4105941
public final class FilterUtil {

    private static Map<Character, Character> replacements;

    static {
        // replacements
        replacements = new HashMap<>();

        replacements.put('1', 'i');
        replacements.put('!', 'i');
        replacements.put('3', 'e');
        replacements.put('4', 'a');
        replacements.put('@', 'a');
        replacements.put('&', 'a');
        replacements.put('$', 's');
        replacements.put('5', 's');
        replacements.put('7', 't');
        replacements.put('0', 'o');
        replacements.put('9', 'g');
        replacements.put('+', 't');
    }

    /**
     * Iterates over a String input and checks whether a cuss word was found in a list, then checks if the word should be ignored (e.g. bass contains the word *ss).
     * @param input
     * @return
     */
    public static ArrayList<String> badWordsFound(List<String> words, String input) {
        if(input == null) {
            return new ArrayList<>();
        }

        int largestWordLength = 0;

        for (String word : words) {
            if (largestWordLength > word.length()) {
                largestWordLength = word.length();
            }
        }

        ArrayList<String> badWords = new ArrayList<>();
        input = input.toLowerCase().replaceAll("[^a-zA-Z]", "");

        // iterate over each letter in the word
        for(int start = 0; start < input.length(); start++) {
            // from each letter, keep going to find bad words until either the end of the sentence is reached, or the max word length is reached.
            for(int offset = 1; offset < (input.length()+1 - start) && offset < largestWordLength; offset++)  {
                String wordToCheck = input.substring(start, start + offset);
                if(words.contains(wordToCheck)) {
                    badWords.add(wordToCheck);
                }
            }
        }

        return badWords;
    }

    public static boolean isBadMessage(List<String> words, String input) {
        List<String> badWords = new ArrayList<>();

        for (char c : replacements.keySet()) {
            if (input.contains(Character.toString(c))) {
                input = input.replace(c, replacements.get(c));
                badWords = badWordsFound(words, input);
                if (badWords.size() > 0) {
                    break;
                }
            }
        }

        return badWords.size() > 0;
    }

}
