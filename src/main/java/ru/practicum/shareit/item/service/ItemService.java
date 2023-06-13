package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto item, long userId);

    ItemDto patchItem(ItemDto item, long itemId, long userId);

    ItemDto findItem(long itemId);

    List<ItemDto> findItemsByUser(long userId);

    List<ItemDto> searchItem(String text);
}
