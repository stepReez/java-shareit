package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemRepository {

    ItemDto createItem(ItemDto itemDto);

    ItemDto patchItem(ItemDto itemDto, long itemId, long userId);

    ItemDto findItem(long itemId);

    List<ItemDto> findItemsByUser(long userId);

    List<ItemDto> searchItem(String text);
}
