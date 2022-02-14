package com.stock.backend.exceptions.UserExceptions;

public class SamePasswordException extends Exception {

    public SamePasswordException(String message) {
        super(message);
    }
}
