package com.walk.or.die.engine;

public class DataException extends Exception {
    public DataException(String msg) {
        super("Error in data provided : " + msg);
    }
} 