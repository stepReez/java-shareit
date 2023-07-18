package ru.practicum.shareit.server.dtoTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.util.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = BookingDto.builder()
                .id(1)
                .start(start)
                .end(end)
                .itemId(1)
                .bookerId(1)
                .status(BookingStatus.WAITING)
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
    }
}
