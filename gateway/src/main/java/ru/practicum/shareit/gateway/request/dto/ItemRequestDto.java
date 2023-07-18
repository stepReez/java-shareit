package ru.practicum.shareit.gateway.request.dto;

import lombok.*;
import ru.practicum.shareit.gateway.item.dto.ItemDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private long id;

    @NotNull
    private String description;

    private long requesterId;

    private LocalDateTime created;

    private List<ItemDto> items;
}
