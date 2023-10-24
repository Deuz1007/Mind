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
                String answerLower = question.answer.toLowerCase();

                // Create a HashMap to store the frequency of each character.
                Map<Character, Integer> charCountMap = new HashMap<>();

                // Iterate over the string and count the frequency of each character.
                for (char c : answerLower.toCharArray()) {
                    if (charCountMap.containsKey(c)) {
                        charCountMap.put(c, charCountMap.get(c) + 1);
                    } else {
                        charCountMap.put(c, 1);
                    }
                }

                // Find the character with the highest frequency.
                int maxCount = 0;
                char maxChar = ' ';

                for (Map.Entry<Character, Integer> entry : charCountMap.entrySet()) {
                    if (entry.getValue() > maxCount) {
                        maxCount = entry.getValue();
                        maxChar = entry.getKey();
                    }
                }

                // Return answer as hint
                return answerLower.replaceAll("[^" + maxChar + "\\s]", "_");
            default:
                return null;
        }
    }
}
