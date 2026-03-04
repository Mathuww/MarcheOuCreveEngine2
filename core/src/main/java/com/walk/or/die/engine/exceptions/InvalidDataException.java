package com.walk.or.die.engine.exceptions;

/**
 * Indicates that the provided data is incorrect or corrupted.
 */
public class InvalidDataException extends Exception {
    /**
     * Constructs a new InvalidDataException with the specified detail message.
     * @param msg The message of the exception.
     */
    public InvalidDataException(String msg) {
        super("Error in data provided : " + msg);
    }
}