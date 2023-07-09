package ru.practicum.shareit.unitTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    private BookingServiceImpl bookingService;

    @BeforeEach
    public void init() {
        bookingService = new BookingServiceImpl(bookingRepository,
                itemRepository,
                userRepository);
    }

    @Nested
    @DisplayName("Create Test")
    class CreateTest {
        @Test
        public void createBookingWithWrongUser() {
            BookingDto bookingDto = BookingDto.builder()
                    .start(LocalDateTime.now())
                    .end(LocalDateTime.MAX)
                    .itemId(1)
                    .build();

            assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDto, 1));
        }

        @Test
        public void createBookingTimeValidationTest() {
            when(itemRepository.findById(anyLong()))
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

            assertThrows(BookingBadRequestException.class, () -> bookingService.createBooking(bookingDto, 1));
        }

        @Test
        public void createBookingUserNotExistTest() {
            when(itemRepository.findById(anyLong()))
                    .thenReturn(Optional.of(Item.builder()
                            .id(1)
                            .owner(1)
                            .build()));

            BookingDto bookingDto = BookingDto.builder()
                    .start(LocalDateTime.now())
                    .end(LocalDateTime.MAX)
                    .itemId(1)
                    .build();

            assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDto, 1));
        }

        @Test
        public void createBookingOwnerTest() {
            when(itemRepository.findById(anyLong()))
                    .thenReturn(Optional.of(Item.builder()
                            .id(1)
                            .owner(1)
                            .available(true)
                            .build()));

            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            BookingDto bookingDto = BookingDto.builder()
                    .start(LocalDateTime.now())
                    .end(LocalDateTime.MAX)
                    .itemId(1)
                    .build();

            assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDto, 1));
        }

        @Test
        public void createBookingNotAvailableTest() {
            when(itemRepository.findById(anyLong()))
                    .thenReturn(Optional.of(Item.builder()
                            .id(1)
                            .owner(1)
                            .available(false)
                            .build()));

            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            BookingDto bookingDto = BookingDto.builder()
                    .start(LocalDateTime.now())
                    .end(LocalDateTime.MAX)
                    .itemId(1)
                    .build();

            assertThrows(BookingBadRequestException.class, () -> bookingService.createBooking(bookingDto, 1));
        }

        @Test
        public void createBookingEndBeforeStartTest() {
            when(itemRepository.findById(anyLong()))
                    .thenReturn(Optional.of(Item.builder()
                            .id(1)
                            .owner(1)
                            .available(true)
                            .build()));

            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            BookingDto bookingDto = BookingDto.builder()
                    .end(LocalDateTime.now())
                    .start(LocalDateTime.MAX)
                    .itemId(1)
                    .build();

            assertThrows(BookingBadRequestException.class, () -> bookingService.createBooking(bookingDto, 1));
        }
    }

    @Nested
    @DisplayName("Update Test")
    class UpdateTest {
        @Test
        public void patchBookingNotExistTest() {
            assertThrows(NotFoundException.class,
                    () -> bookingService.patchBooking(1, false, 1));
        }

        @Test
        public void patchBookingAlreadyAvailableTest() {
            Item item = Item.builder()
                    .id(1)
                    .owner(1)
                    .available(true)
                    .build();
            when(bookingRepository.findById(anyLong()))
                    .thenReturn(Optional.of(Booking.builder()
                            .status(BookingStatus.APPROVED)
                            .item(item)
                            .build()));

            assertThrows(BookingBadRequestException.class,
                    () -> bookingService.patchBooking(1, false, 1));
        }
    }

    @Nested
    @DisplayName("Read test")
    class ReadTest {
        @Test
        public void getBookingNotExistTest() {
            assertThrows(NotFoundException.class, () -> bookingService.getBooking(1, 1));
        }

        @Test
        public void getBookingWithWrongBookerOrOwnerTest() {
            when(bookingRepository.findById(anyLong()))
                    .thenReturn(Optional.of(Booking.builder()
                            .id(1)
                            .booker(User.builder().id(2).build())
                            .item(Item.builder().owner(2).build())
                            .build()));

            assertThrows(NotFoundException.class, () -> bookingService.getBooking(1, 1));
        }

        @Test
        public void getUsersBookingWrongStateTest() {
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            assertThrows(UnknownStateException.class,
                    () -> bookingService.getUsersBooking("UNKNOWN", 1, 1, 10));
        }

        @Test
        public void getOwnerBookingWrongStateTest() {
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            assertThrows(UnknownStateException.class,
                    () -> bookingService.getUsersBooking("UNKNOWN", 1, 1, 10));
        }

        @Test
        public void getUsersBookingStateAll() {
            when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any()))
                    .thenReturn(List.of(Booking.builder().id(1).build()));

            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            List<BookingDtoResponse> list = bookingService.getUsersBooking("ALL", 1, 0, 10);
            BookingDtoResponse booking = list.get(0);

            assertEquals(1, booking.getId());
        }

        @Test
        public void getUsersBookingStateCurrent() {
            when(bookingRepository.findCurrentByBooker(anyLong(), any(), any()))
                    .thenReturn(List.of(Booking.builder().id(1).build()));

            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            List<BookingDtoResponse> list = bookingService.getUsersBooking("CURRENT", 1, 0, 10);
            BookingDtoResponse booking = list.get(0);

            assertEquals(1, booking.getId());
        }

        @Test
        public void getUsersBookingStatePast() {
            when(bookingRepository.findPastByBooker(anyLong(), any(), any()))
                    .thenReturn(List.of(Booking.builder().id(1).build()));

            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            List<BookingDtoResponse> list = bookingService.getUsersBooking("PAST", 1, 0, 10);
            BookingDtoResponse booking = list.get(0);

            assertEquals(1, booking.getId());
        }

        @Test
        public void getUsersBookingStateFuture() {
            when(bookingRepository.findFutureByBooker(anyLong(), any(), any()))
                    .thenReturn(List.of(Booking.builder().id(1).build()));

            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            List<BookingDtoResponse> list = bookingService.getUsersBooking("FUTURE", 1, 0, 10);
            BookingDtoResponse booking = list.get(0);

            assertEquals(1, booking.getId());
        }

        @Test
        public void getUsersBookingStateWaiting() {
            when(bookingRepository.findByStateWaitingByBooker(anyLong(), any()))
                    .thenReturn(List.of(Booking.builder().id(1).build()));

            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            List<BookingDtoResponse> list = bookingService.getUsersBooking("WAITING", 1, 0, 10);
            BookingDtoResponse booking = list.get(0);

            assertEquals(1, booking.getId());
        }

        @Test
        public void getUsersBookingStateRejected() {
            when(bookingRepository.findByStateRejectedByBooker(anyLong(), any()))
                    .thenReturn(List.of(Booking.builder().id(1).build()));

            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            List<BookingDtoResponse> list = bookingService.getUsersBooking("REJECTED", 1, 0, 10);
            BookingDtoResponse booking = list.get(0);

            assertEquals(1, booking.getId());
        }

        @Test
        public void getOwnerBookingStateAll() {
            when(bookingRepository.findAllByOwner(anyLong(), any()))
                    .thenReturn(List.of(Booking.builder().id(1).build()));

            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            List<BookingDtoResponse> list = bookingService.getOwnerBooking("ALL", 1, 0, 10);
            BookingDtoResponse booking = list.get(0);

            assertEquals(1, booking.getId());
        }

        @Test
        public void getOwnerBookingStateCurrent() {
            when(bookingRepository.findCurrentByOwner(anyLong(), any(), any()))
                    .thenReturn(List.of(Booking.builder().id(1).build()));

            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            List<BookingDtoResponse> list = bookingService.getOwnerBooking("CURRENT", 1, 0, 10);
            BookingDtoResponse booking = list.get(0);

            assertEquals(1, booking.getId());
        }

        @Test
        public void getOwnerBookingStatePast() {
            when(bookingRepository.findPastByOwner(anyLong(), any(), any()))
                    .thenReturn(List.of(Booking.builder().id(1).build()));

            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            List<BookingDtoResponse> list = bookingService.getOwnerBooking("PAST", 1, 0, 10);
            BookingDtoResponse booking = list.get(0);

            assertEquals(1, booking.getId());
        }

        @Test
        public void getOwnerBookingStateFuture() {
            when(bookingRepository.findFutureByOwner(anyLong(), any(), any()))
                    .thenReturn(List.of(Booking.builder().id(1).build()));

            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            List<BookingDtoResponse> list = bookingService.getOwnerBooking("FUTURE", 1, 0, 10);
            BookingDtoResponse booking = list.get(0);

            assertEquals(1, booking.getId());
        }

        @Test
        public void getOwnerBookingStateWaiting() {
            when(bookingRepository.findStateByOwner(anyLong(), any(), any()))
                    .thenReturn(List.of(Booking.builder().id(1).build()));

            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            List<BookingDtoResponse> list = bookingService.getOwnerBooking("WAITING", 1, 0, 10);
            BookingDtoResponse booking = list.get(0);

            assertEquals(1, booking.getId());
        }

        @Test
        void getUsersBookingWithoutUserTest() {
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> bookingService.getUsersBooking("ALL", 1, 0, 10));
        }

        @Test
        void getOwnerBookingWithoutUserTest() {
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> bookingService.getOwnerBooking("ALL", 1, 0, 10));
        }

        @Test
        void getUsersBookingBadRequest() {
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            assertThrows(BookingBadRequestException.class,
                    () -> bookingService.getUsersBooking("ALL", 1, -1, 10));
        }

        @Test
        void getOwnerBookingBadRequest() {
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new User()));

            assertThrows(BookingBadRequestException.class,
                    () -> bookingService.getOwnerBooking("ALL", 1, -1, 10));
        }
    }
}
