package com.mindapps.mind.exceptions;

public class FileSizeLimitException extends Exception {
    public FileSizeLimitException() {
        super("File size is more than 1 MiB.");
    }
}
