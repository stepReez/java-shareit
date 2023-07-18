package ru.practicum.shareit.server.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.server.booking.util.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private long id;

    @JsonFormat
    private LocalDateTime start;

    @JsonFormat
    private LocalDateTime end;

    private long itemId;

    private long bookerId;

    private BookingStatus status;
}
