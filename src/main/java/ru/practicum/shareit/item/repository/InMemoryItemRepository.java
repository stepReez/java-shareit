package ru.practicum.shareit.item.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemBadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryItemRepository implements ItemRepository {
    private final List<Item> items = new ArrayList<>();

    private long idCounter = 1;

    UserRepository userRepository;

    @Autowired
    public InMemoryItemRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto createItem(Item item) {
        valid(item);
        item.setId(idCounter);
        items.add(item);
        idCounter++;
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto patchItem(Item item, long itemId, long userId) {
        Item oldItem = items.get((int)itemId - 1);
        if (oldItem.getOwner() == userId) {
            item = patch(oldItem, item);
            items.set((int)itemId - 1, item);
        } else {
            throw new NotFoundException(
                    String.format("User with id %d is not owner of the item with id %d", userId, itemId)
            );
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto findItem(long itemId) {
        return ItemMapper.toItemDto(items.get((int)itemId - 1));
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
            if ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                    item.getAvailable()) {
                itemsByQuery.add(ItemMapper.toItemDto(item));
            }
        }
        return itemsByQuery;
    }

    private void valid(Item item) {
        if (item.getAvailable() == null) {
            throw new ItemBadRequestException("Item can't be unavailable");
        }
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ItemBadRequestException("Item name can't be empty");
        }
        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            throw new ItemBadRequestException("Item description can't be empty");
        }
        for (UserDto user : userRepository.getUsers()) {
            if (user.getId() == item.getOwner()) {
                return;
            }
        }
        throw new NotFoundException(String.format("User with id %d not found", item.getOwner()));
    }

    private Item patch(Item oldItem, Item newItem) {
        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }
        return oldItem;
    }
}
