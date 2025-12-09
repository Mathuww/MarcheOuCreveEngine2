package com.walk.or.die.engine.exceptions;

/**
 * Throw this exception when somenone try to load the wrong font.
 */
public class NotBeautifulFontException extends Exception {
    /**
     * The constructor.
     * @param name
     */
    public NotBeautifulFontException(String name) {
        super("This font is too ugly to be loaded : " + name);
    }
} 