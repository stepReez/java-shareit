package ru.practicum.shareit.server.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.ItemDtoBooking;
import ru.practicum.shareit.server.item.dto.ItemDtoCreate;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.util.Headers;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDtoCreate item, @RequestHeader(Headers.X_SHARER_USER_ID) long userId) {
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestBody ItemDto item,
                             @PathVariable long itemId,
                             @RequestHeader(Headers.X_SHARER_USER_ID) long userId) {
        return itemService.patchItem(item, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoBooking findItem(@PathVariable long itemId, @RequestHeader(Headers.X_SHARER_USER_ID) long userId) {
        return itemService.findItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoBooking> findItemsByUser(@RequestHeader(Headers.X_SHARER_USER_ID) long userId,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        return itemService.findItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size) {
        return itemService.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @PathVariable long itemId,
                                    @RequestHeader(Headers.X_SHARER_USER_ID) long userId) {
        return itemService.createComment(commentDto, itemId, userId);
    }
}
