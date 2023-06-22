package ru.practicum.shareit.item.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemBadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.util.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryItemRepository {
    private final Map<Long, Item> items = new LinkedHashMap<>();

    private long idCounter = 1;

    private final UserRepository userRepository;

    @Autowired
    public InMemoryItemRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public ItemDto createItem(ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        valid(item);
        item.setId(idCounter);
        items.put(idCounter, item);
        idCounter++;
        return ItemMapper.toItemDto(item);
    }

    public ItemDto patchItem(ItemDto itemDto, long itemId, long userId) {
        Item item = ItemMapper.toItem(itemDto);
        Item oldItem = items.get(itemId);
        if (oldItem.getOwner() == userId) {
            item = patch(oldItem, item);
            items.put(itemId, item);
        } else {
            throw new NotFoundException(
                    String.format("User with id %d is not owner of the item with id %d", userId, itemId)
            );
        }
        return ItemMapper.toItemDto(item);
    }

    public ItemDto findItem(long itemId) {
        return ItemMapper.toItemDto(items.get(itemId));
    }

    public List<ItemDto> findItemsByUser(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItem(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                        item.getAvailable())
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
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
        if (userRepository.findAll().stream().noneMatch(userDto -> userDto.getId() == item.getOwner())) {
            throw new NotFoundException(String.format("User with id %d not found", item.getOwner()));
        }
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
