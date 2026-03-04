package com.walk.or.die.engine.exceptions;

/**
 * Indicates that some data is missing.
 */
public class MissingDataException extends InvalidDataException {
    /**
     * Constructs a new MissingDataException.
     * @param msg The message associated with the exception.
     */
    public MissingDataException(String msg) {
        super(msg);
    }
}