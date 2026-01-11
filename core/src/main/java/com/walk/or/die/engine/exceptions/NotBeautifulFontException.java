package com.walk.or.die.engine.exceptions;

/**
 * Throws this exception when someone tries to load the wrong font.
 */
public class NotBeautifulFontException extends Exception {
    /**
     * The constructor.
     * @param name The name of the font.
     */
    public NotBeautifulFontException(String name) {
        super("This font is too ugly to be loaded : " + name);
    }
}