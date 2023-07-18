package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BookingBadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingMapper;
import ru.practicum.shareit.booking.util.BookingStatus;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

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
                itemRepository.findById(bookingDto.getItemId()).orElse(new Item()),
                userRepository.findById(userId).orElse(new User())));
        log.info("Booking with id = {} created", booking.getId());
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public BookingDtoResponse patchBooking(long bookingId, boolean approved, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking not found"));
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
    public List<BookingDtoResponse> getUsersBooking(String state, long userId, int from, int size) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException(String.format("User with id = %d not found", userId));
        }
        List<Booking> bookingList;
        Pageable pageable = PageRequest.of(from / size, size);
        switch (state) {
            case "ALL":
                bookingList = bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable);
                break;
            case "CURRENT":
                bookingList = bookingRepository.findCurrentByBooker(userId, LocalDateTime.now(), pageable);
                break;
            case "PAST":
                bookingList = bookingRepository.findPastByBooker(userId, LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookingList = bookingRepository.findFutureByBooker(userId, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
                bookingList =  bookingRepository.findByStateWaitingByBooker(userId, pageable);
                break;
            case "REJECTED":
                bookingList = bookingRepository.findByStateRejectedByBooker(userId, pageable);
                break;
            default :
                throw new UnknownStateException("Unknown state: " + state);

        }
        return bookingList.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoResponse> getOwnerBooking(String state, long userId, int from, int size) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException(String.format("User with id = %d not found", userId));
        }
        List<Booking> bookingList;
        Pageable pageable = PageRequest.of(from / size, size);
        switch (state) {
            case "ALL":
                bookingList = bookingRepository.findAllByOwner(userId, pageable);
                break;
            case "CURRENT":
                bookingList =  bookingRepository.findCurrentByOwner(userId, LocalDateTime.now(), pageable);
                break;
            case "PAST":
                bookingList = bookingRepository.findPastByOwner(userId, LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookingList = bookingRepository.findFutureByOwner(userId, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
            case "REJECTED":
                bookingList = bookingRepository.findStateByOwner(userId, state, pageable);
                break;
            default :
                throw new UnknownStateException("Unknown state: " + state);

        }
        return bookingList.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    private void valid(BookingDto booking, long id) {
        if (userRepository.findById(booking.getBookerId()).isEmpty()) {
            throw new NotFoundException(String.format("User with id = %d not found", booking.getBookerId()));
        }
        if (itemRepository.findById(booking.getItemId()).isEmpty()) {
            throw new NotFoundException(String.format("Item with id = %d not found", booking.getItemId()));
        }
        if (!itemRepository.findById(booking.getItemId()).get().getAvailable()) {
            throw new BookingBadRequestException("Item unavailable");
        }
        if (booking.getBookerId() == id) {
            throw new NotFoundException("Booker cannot be the owner");
        }
    }
}
