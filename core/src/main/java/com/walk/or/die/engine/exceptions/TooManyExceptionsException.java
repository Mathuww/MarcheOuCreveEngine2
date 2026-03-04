package com.walk.or.die.engine.exceptions;

/**
 * Represents an error where too many exceptions have occurred.
 * <p>
 * DISCLAIMER: This exception should not be thrown as a direct consequence of another {@code TooManyExceptionsException}
 * to avoid an infinite loop of exceptions.
 */
public class TooManyExceptionsException extends Exception {
    /**
     * Constructs a new {@code TooManyExceptionsException} with the specified detail message.
     *
     * @param msg The detail message.
     */
    public TooManyExceptionsException(String msg) {
        super("error : too many acurrate exceptions have occured, " + msg);
    }
}