package com.example.mind.models;

import com.example.mind.utilities.UniqueID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Question {
    public enum QuestionType {
        MULTIPLE_CHOICE,
        TRUE_OR_FALSE,
        IDENTIFICATION
    }

    public String questionId;
    public QuestionType type;
    public String question;
    public String answer;
    public List<String> choices;

    public Question() {
        this.questionId = UniqueID.generate();
        this.choices = new ArrayList<>();
    }

    public Question(QuestionType type, String question, String answer, List<String> choices) {
        this.questionId = UniqueID.generate();
        this.type = type;
        this.question = question;
        this.answer = answer;
        this.choices = choices;
    }

    public static String[] hint(Question question) {
        switch (question.type) {
            case MULTIPLE_CHOICE:
                List<String> notCorrect = new ArrayList<>();
                String[] hints = new String[2];
                // Get all incorrect answers from choices

                for (String choice : question.choices)
                    if (!choice.equals(question.answer)) notCorrect.add(choice);
                // Generate random index

                for (int i = 0; i < 2; i++) {
                    int index = (int) (Math.random() * notCorrect.size());
                    hints[i] = notCorrect.get(index);
                    notCorrect.remove(index);
                }

                return hints;
                // Return the incorrect answer
            case IDENTIFICATION:
                // Storage for unique letters
                Map<Character, Integer> unique = new HashMap<>();
                // Temporary max value
                int max = Integer.MIN_VALUE;
                // Temporary letter with max value
                char maxLetter = ' ';

                for (char letter : question.answer.toLowerCase().toCharArray()) {
                // Traverse each letter
                    // If letter is not recorded yet
                    if (unique.get(letter) == null) {
                        // Get the max value between Integer.MIN_VALUE and 1
                        max = Math.max(max, 1);
                        // Assign new max letter
                        maxLetter = letter;

                        // Add it to the HashMap with its count
                        unique.put(letter, 1);
                    }
                    // If the letter is recorded
                    else {
                        // Get the count of the letter and increment it
                        int count = unique.get(letter) + 1;
                        // Overwrite the count of the letter in the map
                        unique.replace(letter, count);

                        // Identify the max value
                        max = Math.max(max, count);
                        // If the max value changed, there is a new max letter
                        if (max == count)
                            maxLetter = letter;
                    }
                }

                return new String[] { maxLetter + "" };

                // Return answer as hint
            default:
                return null;
        }
    }
}
