package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.OwnerException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryItemRepository implements ItemRepository {

    private final List<Item> items = new ArrayList<>();

    private long idCounter = 0;

    @Override
    public ItemDto createItem(Item item) {
        item.setId(idCounter);
        items.add(item);
        idCounter++;
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto patchItem(Item item, long itemId, long userId) {
        Item oldItem = items.get((int)itemId);
        if (oldItem.getOwner() == userId) {
            items.set((int)itemId, item);
        } else {
            throw new OwnerException(
                    String.format("User with id %d is not owner of the item with id %d", userId, itemId)
            );
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto findItem(long itemId) {
        return ItemMapper.toItemDto(items.get((int)itemId));
    }

    @Override
    public List<ItemDto> findItemsByUser(long userId) {
        List<ItemDto> itemsByUser = new ArrayList<>();
        for (Item item : items) {
            if (item.getOwner() == userId) {
                itemsByUser.add(ItemMapper.toItemDto(item));
            }
        }
        return itemsByUser;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<ItemDto> itemsByQuery = new ArrayList<>();
        for (Item item : items) {
            if (item.getName().contains(text) || item.getDescription().contains(text)) {
                itemsByQuery.add(ItemMapper.toItemDto(item));
            }
        }
        return itemsByQuery;
    }
}
