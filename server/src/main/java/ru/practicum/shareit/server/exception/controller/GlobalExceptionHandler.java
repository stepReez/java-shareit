package ru.practicum.shareit.server.exception.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.server.exception.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EmailNullException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(final EmailNullException e) {
        log.warn("BadRequestException: " + e.getMessage());
        return Map.of("BadRequest", e.getMessage());
    }

    @ExceptionHandler(ItemBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(final ItemBadRequestException e) {
        log.warn("BadRequestException: " + e.getMessage());
        return Map.of("BadRequest", e.getMessage());
    }

    @ExceptionHandler(BookingBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(final BookingBadRequestException e) {
        log.warn("BadRequestException: " + e.getMessage());
        return Map.of("BadRequest", e.getMessage());
    }

    @ExceptionHandler(UnknownStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleBadRequest(final UnknownStateException e) {
        log.warn("Unknown state " + e.getMessage());
        return Map.of("error",  e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final NotFoundException e) {
        log.warn("NotFoundException: " + e.getMessage());
        return Map.of("Not found", e.getMessage());
    }
}
