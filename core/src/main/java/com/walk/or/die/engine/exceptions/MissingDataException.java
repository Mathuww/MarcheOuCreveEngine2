package com.walk.or.die.engine.exceptions;

/**
 * Throws when some datas are missing (it's in the name).
 */
public class MissingDataException extends InvalidDataException {
    /**
     * The constructor.
     * @param msg
     */
    public MissingDataException(String msg) {
        super(msg);
    }
}
