package com.example.mind.interfaces;

public class InvalidQuizCodeException extends Exception {
    public InvalidQuizCodeException() {
        super("Invalid quiz code");
    }
}
