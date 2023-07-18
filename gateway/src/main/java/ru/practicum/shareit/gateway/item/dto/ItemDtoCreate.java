package ru.practicum.shareit.gateway.item.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDtoCreate {
    @NotEmpty(message = "Item name can't be empty")
    private String name;

    @NotEmpty(message = "Item description can't be empty")
    private String description;

    @NotNull(message = "Item can't be unavailable")
    private Boolean available;

    private long requestId;
}
