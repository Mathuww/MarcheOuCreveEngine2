package com.walk.or.die.engine.exceptions;

/**
 * Throw this error when you don't find the behavior.
 */
public class UnexistingBehaviorException extends Exception{
    /**
     * The constructor.
     * @param msg
     */
    public UnexistingBehaviorException(String msg) {
        super(msg);
    }
}
