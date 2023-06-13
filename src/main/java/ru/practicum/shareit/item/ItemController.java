package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ItemBadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @ExceptionHandler(ItemBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HttpStatus handleBadRequest(final ItemBadRequestException e) {
        return HttpStatus.BAD_REQUEST;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpStatus handleNotFound(final NotFoundException e) {
        return HttpStatus.NOT_FOUND;
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody Item item, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@Valid @RequestBody Item item,
                             @PathVariable long itemId,
                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.patchItem(item, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItem(@PathVariable long itemId) {
        return itemService.findItem(itemId);
    }

    @GetMapping
    public List<ItemDto> findItemsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findItemsByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }
}
