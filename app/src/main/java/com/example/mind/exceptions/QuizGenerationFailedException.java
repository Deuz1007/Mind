package com.example.mind.exceptions;

public class QuizGenerationFailedException extends Exception {
    public QuizGenerationFailedException() {
        super("Quiz generation failed");
    }
}
