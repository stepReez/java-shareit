package ru.practicum.shareit.gateway.item.dto;

import lombok.*;
import ru.practicum.shareit.gateway.booking.dto.BookItemRequestDto;

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
