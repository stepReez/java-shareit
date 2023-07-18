package ru.practicum.shareit.server.exception;

public class UnknownStateException extends RuntimeException {
    public UnknownStateException(String s) {
        super(s);
    }
}
