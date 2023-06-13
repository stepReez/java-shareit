package ru.practicum.shareit.item.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemDto createItem(Item item, long userId) {
        item.setOwner(userId);
        ItemDto itemDto = itemRepository.createItem(item);
        log.info("Item with id {} created", itemDto.getId());
        return itemDto;
    }

    @Override
    public ItemDto patchItem(Item item, long itemId, long userId) {
        ItemDto itemDto = itemRepository.patchItem(item, itemId, userId);
        log.info("Item with id {} patched", itemId);
        return itemDto;
    }

    @Override
    public ItemDto findItem(long itemId) {
        ItemDto itemDto = itemRepository.findItem(itemId);
        log.info("Item with id {} found", itemId);
        return itemDto;
    }

    @Override
    public List<ItemDto> findItemsByUser(long userId) {
        List<ItemDto> itemDtoList = itemRepository.findItemsByUser(userId);
        log.info("All items of the user with id {} found", userId);
        return itemDtoList;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        if (!text.isBlank()) {
            itemDtoList = itemRepository.searchItem(text);
        }
        log.info("All items matching \"{}\" found", text);
        return itemDtoList;
    }
}
