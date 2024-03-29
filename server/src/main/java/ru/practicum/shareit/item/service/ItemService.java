package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDtoCreate item, long userId);

    ItemDto patchItem(ItemDto item, long itemId, long userId);

    ItemDtoBooking findItem(long itemId, long userId);

    List<ItemDtoBooking> findItemsByUser(long userId, int from, int size);

    List<ItemDto> searchItem(String text, int from, int size);

    CommentDto createComment(CommentDto commentDto, long itemId, long userId);
}
