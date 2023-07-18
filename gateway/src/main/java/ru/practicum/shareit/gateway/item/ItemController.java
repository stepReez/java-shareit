package ru.practicum.shareit.gateway.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemDto;
import ru.practicum.shareit.gateway.item.dto.ItemDtoCreate;
import ru.practicum.shareit.gateway.util.Headers;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDtoCreate item, @RequestHeader(Headers.X_SHARER_USER_ID) long userId) {
        return itemClient.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@Valid @RequestBody ItemDto item,
                             @PathVariable long itemId,
                             @RequestHeader(Headers.X_SHARER_USER_ID) long userId) {
        return itemClient.patchItem(item, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItem(@PathVariable long itemId, @RequestHeader(Headers.X_SHARER_USER_ID) long userId) {
        return itemClient.findItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findItemsByUser(@Valid @RequestHeader(Headers.X_SHARER_USER_ID) long userId,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                @Positive @RequestParam(defaultValue = "10") int size) {
        return itemClient.findItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@Valid @RequestParam String text,
                                    @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                    @Positive @RequestParam(defaultValue = "10") int size) {
        return itemClient.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestBody CommentDto commentDto,
                                    @PathVariable long itemId,
                                    @RequestHeader(Headers.X_SHARER_USER_ID) long userId) {
        return itemClient.createComment(commentDto, itemId, userId);
    }
}
