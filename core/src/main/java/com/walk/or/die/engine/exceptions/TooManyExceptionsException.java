package com.walk.or.die.engine.exceptions;

/**
 * Throw this exception when there too many exceptions.<br>
 * DISCLAIMER : Don't throw the TooManyExceptionsException due to a TooManyExceptionsException, you would be stuck in a infinit circle of TooManyExceptionsException.
 */
public class TooManyExceptionsException extends Exception {
    public TooManyExceptionsException(String msg) {
        super("error : too many acurrate exceptions have occured, " + msg);
    }
}
