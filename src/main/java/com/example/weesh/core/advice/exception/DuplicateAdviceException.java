package com.example.weesh.core.advice.exception;

public class DuplicateAdviceException extends RuntimeException{
    public DuplicateAdviceException(String message) {
        super(message);
    }
}
