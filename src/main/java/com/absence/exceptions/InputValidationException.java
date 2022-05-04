package com.absence.exceptions;

public class InputValidationException extends Exception {
    public InputValidationException() {}
    public InputValidationException(String message) {
        super(message);
    }
}
