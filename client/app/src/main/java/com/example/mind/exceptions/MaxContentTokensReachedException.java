package com.example.mind.exceptions;

public class MaxContentTokensReachedException extends Exception {
    public static final int MAX_TOKEN = 4096;

    public MaxContentTokensReachedException() {
        super("Content token count exceeded max token count.");
    }
}
