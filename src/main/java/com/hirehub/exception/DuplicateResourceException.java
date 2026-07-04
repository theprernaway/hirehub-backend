package com.hirehub.exception;

// Thrown for things like: user already applied to this job, or email already registered
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
