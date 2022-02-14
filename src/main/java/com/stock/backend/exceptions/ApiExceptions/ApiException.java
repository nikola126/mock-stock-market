package com.stock.backend.exceptions.ApiExceptions;

public class ApiException extends Exception {
    public ApiException(String message) {
        super(message);
    }
}
