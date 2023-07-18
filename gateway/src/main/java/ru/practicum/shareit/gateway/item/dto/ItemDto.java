package ru.practicum.shareit.gateway.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ItemDto {
    private long id;

    private String name;

    private String description;

    private Boolean available;

    private long owner;

    private long requestId;
}
