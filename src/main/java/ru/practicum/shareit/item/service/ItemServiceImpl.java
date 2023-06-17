package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemBadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.util.ItemMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(ItemDto item, long userId) {
        valid(item);
        item.setOwner(userId);
        Item itemDto = itemRepository.save(ItemMapper.toItem(item));
        log.info("Item with id {} created", itemDto.getId());
        return ItemMapper.toItemDto(itemDto);
    }

    @Override
    public ItemDto patchItem(ItemDto item, long itemId, long userId) {
        item.setId(itemId);
        Item itemDto = itemRepository.save(ItemMapper.toItem(item));
        log.info("Item with id {} patched", itemId);
        return ItemMapper.toItemDto(itemDto);
    }

    @Override
    public ItemDto findItem(long itemId) {
        Item itemDto = itemRepository.findById(itemId).get();
        log.info("Item with id {} found", itemId);
        return ItemMapper.toItemDto(itemDto);
    }

    @Override
    public List<ItemDto> findItemsByUser(long userId) {
        List<ItemDto> itemDtoList = itemRepository.findByOwner(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("All items of the user with id {} found", userId);
        return itemDtoList;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        if (!text.isBlank()) {
            itemDtoList = itemRepository.findByNameContainingIgnoreCase(text).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
        log.info("All items matching \"{}\" found", text);
        return itemDtoList;
    }

    private void valid(ItemDto item) {
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
}
