package ru.practicum.shareit.gateway.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private long id;

    private String text;

    private long itemId;

    private String authorName;

    @JsonFormat
    private LocalDateTime created;
}