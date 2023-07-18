package ru.practicum.shareit.server.exception;

public class BookingBadRequestException extends RuntimeException {
    public BookingBadRequestException(String s) {
        super(s);
    }
}
