package com.walk.or.die.engine.exceptions;

/**
 * Throws this exception to show your dissatisfaction.<br>
 * Expresses yourself, no one can censor you.
 */
public class VoluntaryCrashException extends Exception {
    /**
     * The constructor.
     * @param m The message of the exception.
     */
    public VoluntaryCrashException(String m) {
        super(m);
    }
}