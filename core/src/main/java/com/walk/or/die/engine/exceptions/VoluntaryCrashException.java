package com.walk.or.die.engine.exceptions;

/**
 * Throw this exception to show your dissatisfaction.<br>
 * Express yourself, no one can censor you.
 */
public class VoluntaryCrashException extends Exception {
    /**
     * The constructor.
     * @param m
     */
    public VoluntaryCrashException(String m) {
        super(m);
    }
}