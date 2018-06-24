package ru.star.config.exception;

/**
 * Throws when the config is wrong.
 */
public class WrongConfigException extends Exception {
    public WrongConfigException(String s) {
        super(s);
    }
}
