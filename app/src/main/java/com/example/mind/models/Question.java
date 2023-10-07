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

                for (String choice : question.choices)
                    if (!choice.equals(question.answer)) notCorrect.add(choice);

                for (int i = 0; i < 2; i++) {
                    int index = (int) (Math.random() * notCorrect.size());
                    hints[i] = notCorrect.get(index);
                    notCorrect.remove(index);
                }

                return hints;
            case IDENTIFICATION:
                Map<Character, Integer> unique = new HashMap<>();
                int max = Integer.MIN_VALUE;
                char maxLetter = ' ';

                for (char letter : question.answer.toLowerCase().toCharArray()) {
                    if (unique.get(letter) == null) {
                        max = Math.max(max, 1);
                        maxLetter = letter;

                        unique.put(letter, 1);
                    }
                    else {
                        int count = unique.get(letter) + 1;
                        unique.replace(letter, count);

                        max = Math.max(max, count);
                        if (max == count)
                            maxLetter = letter;
                    }
                }

                return new String[] { maxLetter + "" };

            default:
                return null;
        }
    }
}
