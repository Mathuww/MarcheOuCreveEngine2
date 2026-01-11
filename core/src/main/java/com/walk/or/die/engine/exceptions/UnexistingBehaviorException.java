package com.walk.or.die.engine.exceptions;

/**
 * Throws this error when the behavior is not found.
 */
public class UnexistingBehaviorException extends Exception{
    /**
     * The constructor.
     * @param msg The message of the exception.
     */
    public UnexistingBehaviorException(String msg) {
        super(msg);
    }
}