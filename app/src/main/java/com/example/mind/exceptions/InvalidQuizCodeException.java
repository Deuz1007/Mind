package com.example.mind.exceptions;

public class InvalidQuizCodeException extends Exception {
    public InvalidQuizCodeException() {
        super("Invalid quiz code");
    }
}
