package com.stock.backend.exceptions;

public class InsufficientAssetsException extends Exception {
    public InsufficientAssetsException(String message) {
        super(message);
    }
}
