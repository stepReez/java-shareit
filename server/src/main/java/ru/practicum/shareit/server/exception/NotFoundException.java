package ru.practicum.shareit.server.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String s) {
        super(s);
    }
}
