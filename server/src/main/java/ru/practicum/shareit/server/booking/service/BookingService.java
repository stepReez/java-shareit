package ru.practicum.shareit.server.booking.service;

import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {

    BookingDtoResponse createBooking(BookingDto bookingDto, long userId);

    BookingDtoResponse patchBooking(long bookingId, boolean approved, long userId);

    BookingDtoResponse getBooking(long bookingId, long userId);

    List<BookingDtoResponse> getUsersBooking(String state, long userId, int from, int size);

    List<BookingDtoResponse> getOwnerBooking(String state, long userId, int from, int size);
}
