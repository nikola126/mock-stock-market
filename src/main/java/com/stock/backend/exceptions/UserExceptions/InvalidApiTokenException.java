package com.stock.backend.exceptions.UserExceptions;

public class InvalidApiTokenException extends Exception {
    public InvalidApiTokenException(String message) {
        super(message);
    }
}
