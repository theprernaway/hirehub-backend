package com.hirehub.exception;

// Thrown when a user tries to act on a resource they don't own
// (e.g. an employer trying to edit another employer's job)
public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
