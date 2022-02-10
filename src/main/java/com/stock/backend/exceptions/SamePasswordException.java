package com.stock.backend.exceptions;

public class SamePasswordException extends Exception {

    public SamePasswordException(String message) {
        super(message);
    }
}
