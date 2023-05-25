package com.emeraldhieu.toucher.touch;

/**
 * An exception thrown when touch processor fails.
 */
public class TouchProcessorException extends RuntimeException {

    public TouchProcessorException(String message) {
        super(message);
    }

    public TouchProcessorException(String message, Throwable cause) {
        super(message, cause);
    }
}
