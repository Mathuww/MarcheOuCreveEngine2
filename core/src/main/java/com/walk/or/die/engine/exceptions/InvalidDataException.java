package com.walk.or.die.engine.exceptions;

/**
 * Throw this exception when the datas are wrong or corrupted.
 */
public class InvalidDataException extends Exception {
    /**
     * The constructor.
     * @param msg
     */
    public InvalidDataException(String msg) {
        super("Error in data provided : " + msg);
    }
} 