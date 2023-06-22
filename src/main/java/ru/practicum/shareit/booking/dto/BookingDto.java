package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.util.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private long id;

    @JsonFormat
    @NotNull
    @Future(message = "Start of booking cannot be in the past")
    private LocalDateTime start;

    @JsonFormat
    @NotNull
    @Future(message = "End of booking cannot be in the past")
    private LocalDateTime end;

    private long itemId;

    private long bookerId;

    private BookingStatus status;
}
