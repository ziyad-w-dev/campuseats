package com.campuseats.exception;

public class QuantityLimitException extends RuntimeException {

    public QuantityLimitException(String message) {
        super(message);
    }
}
