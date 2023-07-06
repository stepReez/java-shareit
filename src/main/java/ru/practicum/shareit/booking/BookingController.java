package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.Headers;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse crateBooking(@Valid @RequestBody BookingDto bookingDto,
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
                                                    @RequestHeader(Headers.X_SHARER_USER_ID) long userId,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size) {
        return bookingService.getUsersBooking(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getOwnerBooking(@RequestParam(defaultValue = "ALL") String state,
                                                    @RequestHeader(Headers.X_SHARER_USER_ID) long userId,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size) {
        return bookingService.getOwnerBooking(state, userId, from, size);
    }
}
