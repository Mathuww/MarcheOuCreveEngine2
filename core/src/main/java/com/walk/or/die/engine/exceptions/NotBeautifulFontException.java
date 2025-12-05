package com.walk.or.die.engine.exceptions;

public class NotBeautifulFontException extends Exception {
    public NotBeautifulFontException(String name) {
        super("This font is too ugly to be loaded : " + name);
    }
} 