package ru.practicum.shareit.server.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.booking.dto.BookingDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDtoBooking {
    private long id;

    private String name;

    private String description;

    private Boolean available;

    private long owner;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> comments;
}
