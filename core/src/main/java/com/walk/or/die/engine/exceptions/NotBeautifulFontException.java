package com.walk.or.die.engine.exceptions;

/**
 * Indicates that an attempt was made to load an unsuitable font.
 */
public class NotBeautifulFontException extends Exception {
    /**
     * Constructs a new {@code NotBeautifulFontException}.
     * @param name The name of the font that caused the exception.
     */
    public NotBeautifulFontException(String name) {
        super("This font is too ugly to be loaded : " + name);
    }
}