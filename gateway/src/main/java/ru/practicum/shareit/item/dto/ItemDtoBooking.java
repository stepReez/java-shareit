package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

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

    private BookItemRequestDto lastBooking;

    private BookItemRequestDto nextBooking;

    private List<CommentDto> comments;
}
