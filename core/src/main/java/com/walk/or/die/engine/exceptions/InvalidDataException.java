package com.walk.or.die.engine.exceptions;

/**
 * Throws this exception when the data are wrong or corrupted.
 */
public class InvalidDataException extends Exception {
    /**
     * The constructor.
     * @param msg The message of the exception.
     */
    public InvalidDataException(String msg) {
        super("Error in data provided : " + msg);
    }
}