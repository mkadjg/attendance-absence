package com.absence.exceptions;

public class SendEmailException extends Exception {
    public SendEmailException() {}
    public SendEmailException(String message) {
        super(message);
    }
}
