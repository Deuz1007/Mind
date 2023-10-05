package com.example.mind.models;

import com.example.mind.utilities.UniqueID;

import java.util.ArrayList;
import java.util.List;

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
}
