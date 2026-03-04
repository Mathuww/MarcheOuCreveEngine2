package com.walk.or.die.engine.exceptions;

/**
 * Thrown to indicate dissatisfaction.<br>
 * It provides a way to express oneself without censorship.
 */
public class VoluntaryCrashException extends Exception {
    /**
     * Constructs a new {@code VoluntaryCrashException}.
     * @param m The message of the exception.
     */
    public VoluntaryCrashException(String m) {
        super(m);
    }
}