package com.walk.or.die.engine.exceptions;

/**
 * Throws this exception when there are too many exceptions.<br>
 * DISCLAIMER : Don't throw the TooManyExceptionsException due to a TooManyExceptionsException, you would be stuck in an infinite circle of TooManyExceptionsException.
 */
public class TooManyExceptionsException extends Exception {
    /**
     * @param msg The message.
     */
    public TooManyExceptionsException(String msg) {
        super("error : too many acurrate exceptions have occured, " + msg);
    }
}