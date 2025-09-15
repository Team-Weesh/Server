package com.example.weesh.core.user.exception;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String message, String value) {
        super(String.format(message+value, value));
    }
}