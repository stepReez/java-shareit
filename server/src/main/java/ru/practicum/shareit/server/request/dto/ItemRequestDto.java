package ru.practicum.shareit.server.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.server.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private long id;

    private String description;

    private long requesterId;

    private LocalDateTime created;

    private List<ItemDto> items;
}
