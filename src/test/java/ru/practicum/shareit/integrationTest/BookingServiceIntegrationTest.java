package ru.practicum.shareit.integrationTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {

    @Autowired
    ItemServiceImpl itemService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    BookingServiceImpl bookingService;

    @Test
    public void createBookingTest() {
        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("qwe@qwe.com")
                .build();

        UserDto userDto1 = userService.createUser(userDto);

        UserDto userDtoBooker = UserDto.builder()
                .name("Mr. Booker")
                .email("unique@qwe.com")
                .build();

        UserDto userDtoBooker1 = userService.createUser(userDtoBooker);

        ItemDtoCreate itemDtoCreate = ItemDtoCreate.builder()
                .name("ItemName")
                .description("Description")
                .available(true)
                .build();

        ItemDto itemDto1 = itemService.createItem(itemDtoCreate, userDto1.getId());

        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.MAX)
                .itemId(itemDto1.getId())
                .bookerId(userDtoBooker1.getId())
                .build();

        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(bookingDto, userDtoBooker1.getId());

        assertThat(bookingDto.getStart(), equalTo(bookingDtoResponse.getStart()));
        assertThat(bookingDto.getEnd(), equalTo(bookingDtoResponse.getEnd()));
        assertThat(bookingDto.getItemId(), equalTo(bookingDtoResponse.getItem().getId()));
        assertThat(bookingDto.getBookerId(), equalTo(bookingDtoResponse.getBooker().getId()));
    }

    @Test
    public void patchBookingTest() {
        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("qwe@qwe.com")
                .build();

        UserDto userDto1 = userService.createUser(userDto);

        UserDto userDtoBooker = UserDto.builder()
                .name("Mr. Booker")
                .email("unique@qwe.com")
                .build();

        UserDto userDtoBooker1 = userService.createUser(userDtoBooker);

        ItemDtoCreate itemDtoCreate = ItemDtoCreate.builder()
                .name("ItemName")
                .description("Description")
                .available(true)
                .build();

        ItemDto itemDto1 = itemService.createItem(itemDtoCreate, userDto1.getId());

        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.MAX)
                .itemId(itemDto1.getId())
                .bookerId(userDtoBooker1.getId())
                .build();

        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(bookingDto, userDtoBooker1.getId());

        BookingDtoResponse booking = bookingService.patchBooking(bookingDtoResponse.getId(), true, userDto1.getId());

        assertThat(booking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getBookingTest() {
        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("qwe@qwe.com")
                .build();

        UserDto userDto1 = userService.createUser(userDto);

        UserDto userDtoBooker = UserDto.builder()
                .name("Mr. Booker")
                .email("unique@qwe.com")
                .build();

        UserDto userDtoBooker1 = userService.createUser(userDtoBooker);

        ItemDtoCreate itemDtoCreate = ItemDtoCreate.builder()
                .name("ItemName")
                .description("Description")
                .available(true)
                .build();

        ItemDto itemDto1 = itemService.createItem(itemDtoCreate, userDto1.getId());

        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.MAX)
                .itemId(itemDto1.getId())
                .bookerId(userDtoBooker1.getId())
                .build();

        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(bookingDto, userDtoBooker1.getId());

        BookingDtoResponse bookingDtoGet = bookingService.getBooking(bookingDtoResponse.getId(), userDto1.getId());

        assertThat(bookingDto.getStart(), equalTo(bookingDtoGet.getStart()));
        assertThat(bookingDto.getEnd(), equalTo(bookingDtoGet.getEnd()));
        assertThat(bookingDto.getItemId(), equalTo(bookingDtoGet.getItem().getId()));
        assertThat(bookingDto.getBookerId(), equalTo(bookingDtoGet.getBooker().getId()));
    }
}
