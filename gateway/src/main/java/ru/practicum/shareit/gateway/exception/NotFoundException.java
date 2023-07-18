package ru.practicum.shareit.gateway.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String s) {
        super(s);
    }
}
