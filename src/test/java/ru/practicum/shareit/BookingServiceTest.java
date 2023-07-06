package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.exception.BookingBadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    BookingServiceImpl bookingService;

    @BeforeEach
    public void init() {
        bookingService = new BookingServiceImpl(bookingRepository,
                itemRepository,
                userRepository);
    }

    @Test
    public void createBookingWithWrongUser() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.MAX)
                .itemId(1)
                .build();

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDto, 1));
    }

    @Test
    public void createBookingTimeValidationTest() {
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(Item.builder()
                        .id(1)
                        .owner(1)
                        .build()));

        LocalDateTime localDateTimeNow = LocalDateTime.now();
        BookingDto bookingDto = BookingDto.builder()
                .start(localDateTimeNow)
                .end(localDateTimeNow)
                .itemId(1)
                .build();

        Assertions.assertThrows(BookingBadRequestException.class, () -> bookingService.createBooking(bookingDto, 1));
    }

    @Test
    public void createBookingUserNotExistTest() {
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(Item.builder()
                        .id(1)
                        .owner(1)
                        .build()));

        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.MAX)
                .itemId(1)
                .build();

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDto, 1));
    }

    @Test
    public void createBookingOwnerTest() {
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(Item.builder()
                        .id(1)
                        .owner(1)
                        .available(true)
                        .build()));

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));

        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.MAX)
                .itemId(1)
                .build();

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDto, 1));
    }

    @Test
    public void createBookingNotAvailableTest() {
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(Item.builder()
                        .id(1)
                        .owner(1)
                        .available(false)
                        .build()));

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));

        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.MAX)
                .itemId(1)
                .build();

        Assertions.assertThrows(BookingBadRequestException.class, () -> bookingService.createBooking(bookingDto, 1));
    }

    @Test
    public void createBookingEndBeforeStartTest() {
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(Item.builder()
                        .id(1)
                        .owner(1)
                        .available(true)
                        .build()));

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));

        BookingDto bookingDto = BookingDto.builder()
                .end(LocalDateTime.now())
                .start(LocalDateTime.MAX)
                .itemId(1)
                .build();

        Assertions.assertThrows(BookingBadRequestException.class, () -> bookingService.createBooking(bookingDto, 1));
    }

    @Test
    public void patchBookingNotExistTest() {
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.patchBooking(1, false, 1));
    }

    @Test
    public void patchBookingAlreadyAvailableTest() {
        Item item = Item.builder()
                .id(1)
                .owner(1)
                .available(true)
                .build();
        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(Booking.builder()
                        .status(BookingStatus.APPROVED)
                        .item(item)
                        .build()));

        Assertions.assertThrows(BookingBadRequestException.class,
                () -> bookingService.patchBooking(1, false, 1));
    }

    @Test
    public void getBookingNotExistTest() {
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getBooking(1, 1));
    }

    @Test
    public void getBookingWithWrongBookerOrOwnerTest() {
        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(Booking.builder()
                        .id(1)
                        .booker(User.builder().id(2).build())
                        .item(Item.builder().owner(2).build())
                        .build()));

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getBooking(1, 1));
    }

    @Test
    public void getUsersBookingWrongStateTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));

        Assertions.assertThrows(UnknownStateException.class,
                () -> bookingService.getUsersBooking("UNKNOWN", 1, 1, 10));
    }

    @Test
    public void getOwnerBookingWrongStateTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));

        Assertions.assertThrows(UnknownStateException.class,
                () -> bookingService.getUsersBooking("UNKNOWN", 1, 1, 10));
    }
}
