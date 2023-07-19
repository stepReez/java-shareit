package ru.practicum.shareit.integrationTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.util.BookingStatus;
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
    private ItemServiceImpl itemService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private BookingServiceImpl bookingService;

    private BookingDto bookingDto;

    private UserDto userDto1;

    private UserDto userDtoBooker1;

    @BeforeEach
    public void init() {
        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("qwe@qwe.com")
                .build();

        userDto1 = userService.createUser(userDto);

        UserDto userDtoBooker = UserDto.builder()
                .name("Mr. Booker")
                .email("unique@qwe.com")
                .build();

        userDtoBooker1 = userService.createUser(userDtoBooker);

        ItemDtoCreate itemDtoCreate = ItemDtoCreate.builder()
                .name("ItemName")
                .description("Description")
                .available(true)
                .build();

        ItemDto itemDto1 = itemService.createItem(itemDtoCreate, userDto1.getId());

        bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.MAX)
                .itemId(itemDto1.getId())
                .bookerId(userDtoBooker1.getId())
                .build();
    }

    @Test
    public void createBookingTest() {


        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(bookingDto, userDtoBooker1.getId());

        test(bookingDtoResponse);
    }

    @Test
    public void patchBookingTest() {
        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(bookingDto, userDtoBooker1.getId());

        BookingDtoResponse booking = bookingService.patchBooking(bookingDtoResponse.getId(), true, userDto1.getId());

        assertThat(booking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    public void getBookingTest() {
        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(bookingDto, userDtoBooker1.getId());

        BookingDtoResponse bookingDtoGet = bookingService.getBooking(bookingDtoResponse.getId(), userDto1.getId());

        test(bookingDtoGet);
    }

    private void test(BookingDtoResponse bookingDtoResponse) {
        assertThat(bookingDto.getStart(), equalTo(bookingDtoResponse.getStart()));
        assertThat(bookingDto.getEnd(), equalTo(bookingDtoResponse.getEnd()));
        assertThat(bookingDto.getItemId(), equalTo(bookingDtoResponse.getItem().getId()));
        assertThat(bookingDto.getBookerId(), equalTo(bookingDtoResponse.getBooker().getId()));
    }
}
