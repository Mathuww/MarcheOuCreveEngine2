package com.walk.or.die.engine.exceptions;

/**
 * Throws when some data is missing (it's in the name).
 */
public class MissingDataException extends InvalidDataException {
    /**
     * The constructor.
     * @param msg The message associated with the exception.
     */
    public MissingDataException(String msg) {
        super(msg);
    }
}