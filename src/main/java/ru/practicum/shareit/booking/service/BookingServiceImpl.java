package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingMapper;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.exception.BookingBadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    @Override
    public BookingDtoResponse createBooking(BookingDto bookingDto, long userId) {
        bookingDto.setBookerId(userId);
        bookingDto.setStatus(BookingStatus.WAITING);
        if (itemRepository.findById(bookingDto.getItemId()).isEmpty()) {
            throw new NotFoundException(String.format("Item with id = %d not found", bookingDto.getItemId()));
        }
        valid(bookingDto, itemRepository.findById(bookingDto.getItemId()).get().getOwner());
        Booking booking = bookingRepository.save(BookingMapper.toBooking(
                bookingDto,
                itemRepository.findById(bookingDto.getItemId()).get(),
                userRepository.findById(userId).get()));
        log.info("Booking with id = {} created", booking.getId());
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public BookingDtoResponse patchBooking(long bookingId, boolean approved, long userId) {
        Booking booking = bookingRepository.findById(bookingId).get();
        Item item = booking.getItem();
        if (item.getOwner() == userId && booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingBadRequestException("Cannot change approved status");
        } else if (item.getOwner() == userId) {
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
            bookingRepository.save(booking);
        } else {
            throw new NotFoundException("Booking not found");
        }
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public BookingDtoResponse getBooking(long bookingId, long userId) {
        Booking booking;
        if (bookingRepository.findById(bookingId).isPresent()) {
            booking = bookingRepository.findById(bookingId).get();
        } else {
            throw new NotFoundException("Booking not found");
        }
        Item item = booking.getItem();
        User user = booking.getBooker();
        if (user.getId() == userId || item.getOwner() == userId) {
            log.info("Booking with id = {} found", booking.getId());
        } else {
            throw new NotFoundException("Not found");
        }
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public List<BookingDtoResponse> getUsersBooking(String state, long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException(String.format("User with id = %d not found", userId));
        }
        switch (state) {
            case "ALL":
                return bookingRepository.findByBookerIdOrderByStartDesc(userId).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findCurrentByBooker(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findPastByBooker(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findFutureByBooker(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findByStateWaitingByBooker(userId).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findByStateRejectedByBooker(userId).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            default :
                throw new UnknownStateException("Unknown state: " + state);

        }
    }

    @Override
    public List<BookingDtoResponse> getOwnerBooking(String state, long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException(String.format("User with id = %d not found", userId));
        }
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByOwner(userId).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findCurrentByOwner(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findPastByOwner(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findFutureByOwner(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case "WAITING":
            case "REJECTED":
                return bookingRepository.findStateByOwner(userId, state).stream()
                        .map(BookingMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            default :
                throw new UnknownStateException("Unknown state: " + state);

        }
    }

    private void valid(BookingDto booking, long id) {
        if (booking.getStart() == null || booking.getEnd() == null) {
            throw new BookingBadRequestException("End or start of booking cannot be null");
        }
        if (booking.getStart().equals(booking.getEnd())) {
            throw new BookingBadRequestException("End and start of booking cannot be equal");
        }
        if (userRepository.findById(booking.getBookerId()).isEmpty()) {
            throw new NotFoundException(String.format("User with id = %d not found", booking.getBookerId()));
        }
        if (itemRepository.findById(booking.getItemId()).isEmpty()) {
            throw new NotFoundException(String.format("Item with id = %d not found", booking.getItemId()));
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new BookingBadRequestException("End of booking cannot be in the past");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new BookingBadRequestException("Start of booking cannot be in the past");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new BookingBadRequestException("End of booking cannot be before the start");
        }
        if (!itemRepository.findById(booking.getItemId()).get().getAvailable()) {
            throw new BookingBadRequestException("Item unavailable");
        }
        if (booking.getBookerId() == id) {
            throw new NotFoundException("Booker cannot be the owner");
        }
    }
}
