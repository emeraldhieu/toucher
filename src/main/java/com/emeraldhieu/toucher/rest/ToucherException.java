package com.emeraldhieu.toucher.rest;

public class ToucherException extends RuntimeException {

    public ToucherException(String message) {
        super(message);
    }

    public ToucherException(String message, Throwable cause) {
        super(message, cause);
    }
}
