package com.example.mind.models;

import com.example.mind.utilities.UniqueID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    public static String hint(Question question) {
        switch (question.type) {
            case MULTIPLE_CHOICE:
                // Get all incorrect answers from choices
                List<String> notCorrect = question.choices
                        .stream()
                        .filter(choice -> !choice.equals(question.answer))
                        .collect(Collectors.toList());

                // Generate random index
                int index = (int) (Math.random() * notCorrect.size());

                // Return the incorrect answer
                return notCorrect.get(index);
            case IDENTIFICATION:
                // Storage for unique letters
                Map<Character, Integer> unique = new HashMap<>();
                // Temporary max value
                int max = Integer.MIN_VALUE;
                // Temporary letter with max value
                char maxLetter = ' ';
                String answerLower = question.answer.toLowerCase();

                // Traverse each letter
                for (char letter : answerLower.toCharArray()) {
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

                // Return answer as hint
                return answerLower.replaceAll("[^" + maxLetter + "]", "_");
            default:
                return null;
        }
    }
}
