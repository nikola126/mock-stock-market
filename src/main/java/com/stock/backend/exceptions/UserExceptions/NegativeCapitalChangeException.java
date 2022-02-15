package com.stock.backend.exceptions.UserExceptions;

public class NegativeCapitalChangeException extends Exception {
    public NegativeCapitalChangeException(String message) {
        super(message);
    }
}
