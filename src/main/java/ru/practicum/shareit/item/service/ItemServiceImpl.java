package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.comment.util.CommentMapper;
import ru.practicum.shareit.exception.ItemBadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.util.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final RequestRepository requestRepository;

    @Override
    public ItemDto createItem(ItemDtoCreate itemDtoCreate, long userId) {
        ItemDto item = ItemMapper.fromCrateToItemDto(itemDtoCreate);
        item.setOwner(userId);
        valid(item);
        ItemRequest itemRequest = requestRepository.findById(itemDtoCreate.getRequestId()).orElse(null);
        if (itemRequest != null) {
            item.setRequestId(itemDtoCreate.getRequestId());
        }
        Item itemDto = itemRepository.save(ItemMapper.toItemCreate(item, itemRequest));
        log.info("Item with id {} created", itemDto.getId());
        return ItemMapper.toItemDto(itemDto);
    }

    @Override
    public ItemDto patchItem(ItemDto item, long itemId, long userId) {
        Item newItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        Item itemDto;
        if (newItem.getOwner() == userId) {
            patch(newItem, ItemMapper.toItem(item));
            newItem.setId(itemId);
            itemDto = itemRepository.save(newItem);
            log.info("Item with id {} patched", itemId);
        } else {
            throw new NotFoundException(
                    String.format("User with id %d is not owner of the item with id %d", userId, itemId)
            );
        }
        return ItemMapper.toItemDto(itemDto);
    }

    @Override
    public ItemDtoBooking findItem(long itemId, long userId) {
        Item item;
        ItemDtoBooking itemDtoBooking;
        if (itemRepository.findById(itemId).isPresent()) {
            item = itemRepository.findById(itemId).get();
            itemDtoBooking = ItemMapper.toItemDtoBooking(item);
            List<Booking> nextBookings = new ArrayList<>();
            List<Booking> lastBookings = new ArrayList<>();
            if (item.getOwner() == userId) {
                lastBookings = bookingRepository.getLastBookingByItemId(itemId, LocalDateTime.now());
                nextBookings = bookingRepository.getNextBookingByItemId(itemId, LocalDateTime.now());
            }

            if (!lastBookings.isEmpty())
                itemDtoBooking.setLastBooking(BookingMapper.toBookingDto(lastBookings.get(0)));
            if (!nextBookings.isEmpty())
                itemDtoBooking.setNextBooking(BookingMapper.toBookingDto(nextBookings.get(0)));


            itemDtoBooking.setComments(commentRepository.findByItemId(itemId).stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList()));

        } else {
            throw new NotFoundException(String.format("Item with id = %d not found", itemId));
        }
        log.info("Item with id {} found", itemId);
        return itemDtoBooking;
    }

    @Override
    public List<ItemDtoBooking> findItemsByUser(long userId, int from, int size) {
        if (from < 0) {
            throw new ItemBadRequestException("Bad request");
        }
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemDtoBooking> itemDtoList = itemRepository.findByOwner(userId, pageable).stream()
                .map(ItemMapper::toItemDtoBooking)
                .collect(Collectors.toList());
        itemDtoList.stream()
                .filter(itemDtoBooking ->
                        !bookingRepository.getNextBookingByItemId(
                                itemDtoBooking.getId(), LocalDateTime.now()).isEmpty())
                .forEach(itemDtoBooking ->
                itemDtoBooking.setNextBooking(
                        BookingMapper.toBookingDto(
                        bookingRepository.getNextBookingByItemId(itemDtoBooking.getId(), LocalDateTime.now()).get(0))
                )
        );
        itemDtoList.stream()
                .filter(itemDtoBooking ->
                        !bookingRepository.getLastBookingByItemId(
                                itemDtoBooking.getId(), LocalDateTime.now()).isEmpty())
                .forEach(itemDtoBooking ->
                itemDtoBooking.setLastBooking(
                        BookingMapper.toBookingDto(
                        bookingRepository.getLastBookingByItemId(itemDtoBooking.getId(), LocalDateTime.now()).get(0))
                )
        );
        itemDtoList.stream()
                .filter(itemDtoBooking -> !commentRepository.findByItemId(itemDtoBooking.getId()).isEmpty())
                .forEach(itemDtoBooking ->
                itemDtoBooking.setComments(commentRepository.findByItemId(itemDtoBooking.getId()).stream()
                .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList())));
        log.info("All items of the user with id {} found", userId);
        return itemDtoList;
    }

    @Override
    public List<ItemDto> searchItem(String text, int from, int size) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        if (!text.isBlank()) {
            if (from < 0) {
                throw new ItemBadRequestException("Bad request");
            }
            Pageable pageable = PageRequest.of(from / size, size);
            itemDtoList = itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text,
                            text,
                            pageable)
                    .stream()
                    .filter(Item::getAvailable)
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
        log.info("All items matching \"{}\" found", text);
        return itemDtoList;
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, long itemId, long userId) {
        commentDto.setCreated(LocalDateTime.now());
        if (bookingRepository.getBookingByItemAndUser(itemId, userId).isEmpty()) {
            throw new ItemBadRequestException(
                    String.format("User with id = %d did not book item with id = %d", userId, itemId));
        }
        if (commentDto.getText().isBlank()) {
            throw new ItemBadRequestException("Text cannot be blank");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, item, user));
        return CommentMapper.toCommentDto(comment);
    }

    private void valid(ItemDto item) {
        if (userRepository.findById(item.getOwner()).isEmpty()) {
            throw new NotFoundException(String.format("User with id %d not found", item.getOwner()));
        }
    }

    private void patch(Item oldItem, Item newItem) {
        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }
    }
}
