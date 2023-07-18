package ru.practicum.shareit.gateway.exception;

public class UnknownStateException extends RuntimeException {
    public UnknownStateException(String s) {
        super(s);
    }
}
