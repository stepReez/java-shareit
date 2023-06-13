package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    ItemDto createItem(Item item);

    ItemDto patchItem(Item item, long itemId, long userId);

    ItemDto findItem(long itemId);

    List<ItemDto> findItemsByUser(long userId);

    public List<ItemDto> searchItem(String text);
}
