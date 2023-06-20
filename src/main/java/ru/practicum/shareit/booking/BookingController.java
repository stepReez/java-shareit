package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.Headers;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse crateBooking(@RequestBody BookingDto bookingDto,
                                           @RequestHeader(Headers.X_SHARER_USER_ID) long userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse patchBooking(@PathVariable long bookingId,
                                   @RequestParam boolean approved,
                                   @RequestHeader(Headers.X_SHARER_USER_ID) long userId) {
        return bookingService.patchBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(@PathVariable long bookingId,
                                         @RequestHeader(Headers.X_SHARER_USER_ID) long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoResponse> getUsersBooking(@RequestParam(defaultValue = "ALL") String state,
                                            @RequestHeader(Headers.X_SHARER_USER_ID) long userId) {
        return bookingService.getUsersBooking(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getOwnerBooking(@RequestParam(defaultValue = "ALL") String state,
                                            @RequestHeader(Headers.X_SHARER_USER_ID) long userId) {
        return bookingService.getOwnerBooking(state, userId);
    }
}
