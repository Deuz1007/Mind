package com.example.mind.exceptions;

public class RateLimitException extends Exception {
    public RateLimitException() {
        super("Due to many quiz generation requests, rate limit was reached. Please try generating a new quiz later.");
    }
}
