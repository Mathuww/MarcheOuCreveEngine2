package com.walk.or.die.engine.exceptions;

/**
 * Thrown when a behavior is not found.
 */
public class UnexistingBehaviorException extends Exception{
    /**
     * Constructs a new {@code UnexistingBehaviorException} with the specified detail message.
     * @param msg The detail message.
     */
    public UnexistingBehaviorException(String msg) {
        super(msg);
    }
}