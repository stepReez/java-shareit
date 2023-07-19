package ru.practicum.shareit.exception;

public class BookingBadRequestException extends RuntimeException {
    public BookingBadRequestException(String s) {
        super(s);
    }
}
