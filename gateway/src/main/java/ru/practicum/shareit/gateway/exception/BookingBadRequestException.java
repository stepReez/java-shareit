package ru.practicum.shareit.gateway.exception;

public class BookingBadRequestException extends RuntimeException {
    public BookingBadRequestException(String s) {
        super(s);
    }
}
