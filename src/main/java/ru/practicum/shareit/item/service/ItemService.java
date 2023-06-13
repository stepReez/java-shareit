package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto createItem(Item item, long userId);

    ItemDto patchItem(Item item, long itemId, long userId);

    ItemDto findItem(long itemId);

    List<ItemDto> findItemsByUser(long userId);

    List<ItemDto> searchItem(String text);
}
