package com.mindapps.mind.exceptions;

public class APIErrorException extends Exception {
    public APIErrorException() {
        super("There was a problem with the API. Please generating a new quiz later");
    }
}
