package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.ItemBadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    RequestRepository requestRepository;

    ItemServiceImpl itemService;

    @BeforeEach
    public void init() {
        itemService = new ItemServiceImpl(itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                requestRepository);
    }

    @Test
    public void createItemTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(User.builder()
                        .id(1)
                        .name("Alex")
                        .email("qwe@qwe.com")
                        .build()));

        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(Item.builder()
                        .id(1)
                        .name("ItemName")
                        .description("Description")
                        .available(true)
                        .owner(1)
                        .build());

        ItemDtoCreate itemDtoCreate = ItemDtoCreate.builder()
                .name("ItemName")
                .description("Description")
                .available(true)
                .build();

        ItemDto itemDto = itemService.createItem(itemDtoCreate, 1);

        Assertions.assertEquals(1, itemDto.getId());
        Assertions.assertEquals(1, itemDto.getOwner());
        Assertions.assertEquals("ItemName", itemDto.getName());
        Assertions.assertEquals("Description", itemDto.getDescription());
        Assertions.assertEquals(true, itemDto.getAvailable());
    }

    @Test
    public void itemCreateWithWrongUser() {
        ItemDtoCreate itemDtoCreate = ItemDtoCreate.builder()
                .name("ItemName")
                .description("Description")
                .available(true)
                .build();

        Assertions.assertThrows(NotFoundException.class, () -> itemService.createItem(itemDtoCreate, 1));
    }

    @Test
    public void itemUpdateNotFoundTest() {
        ItemDto itemDto = ItemDto.builder()
                .name("New Name")
                .build();

        Assertions.assertThrows(NotFoundException.class, () -> itemService.patchItem(itemDto, 1, 2));
    }

    @Test
    public void itemUpdateWithWrongUserTest() {
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(Item.builder()
                        .id(1)
                        .name("ItemName")
                        .description("Description")
                        .available(true)
                        .owner(1)
                        .build()));

        ItemDto itemDto = ItemDto.builder()
                .name("New Name")
                .build();

        Assertions.assertThrows(NotFoundException.class, () -> itemService.patchItem(itemDto, 1, 2));
    }

    @Test
    public void findNotExistingItemTest() {
        Assertions.assertThrows(NotFoundException.class, () -> itemService.findItem(1,1));
    }

    @Test
    public void findItemsByUserWrongPageTest() {
        Assertions.assertThrows(ItemBadRequestException.class,
                () -> itemService.findItemsByUser(1, -1, 10));
    }

    @Test
    public void searchItemWithWrongPageTest() {
        Assertions.assertThrows(ItemBadRequestException.class,
                () -> itemService.searchItem("123", -1, 10));
    }

    @Test
    public void createCommentUserWithNoBookTest() {
        CommentDto commentDto = CommentDto.builder()
                .text("Not Blank Comment")
                .itemId(1)
                .authorName("Max")
                .created(LocalDateTime.now())
                .build();
        Assertions.assertThrows(ItemBadRequestException.class,
                () -> itemService.createComment(commentDto, 1, 1));
    }

    @Test
    public void createCommentWithBlankText() {
        CommentDto commentDto = CommentDto.builder()
                .text(" ")
                .itemId(1)
                .authorName("Max")
                .created(LocalDateTime.now())
                .build();

        Mockito
                .when(bookingRepository.getBookingByItemAndUser(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(List.of(Booking.builder()
                        .build()));

        Assertions.assertThrows(ItemBadRequestException.class,
                () -> itemService.createComment(commentDto, 1, 1));
    }

    @Test
    void createCommentTest() {
        Mockito
                .when(bookingRepository.getBookingByItemAndUser(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(List.of(new Booking()));

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(User.builder().name("Max").build()));

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(Item.builder().id(56).build()));

        Comment comment = Comment.builder()
                .id(1)
                .text("qwe")
                .item(Item.builder().id(56).build())
                .author(User.builder().name("Max").build())
                .created(LocalDateTime.now().plusDays(1))
                .build();

        Mockito
                .when(commentRepository.save(Mockito.any()))
                .thenReturn(comment);

        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .text("qwe")
                .itemId(56)
                .authorName("Max")
                .created(LocalDateTime.now().plusDays(1))
                .build();

        CommentDto commentDto1 = itemService.createComment(commentDto, 56, 1);

        Assertions.assertEquals(1, commentDto.getId());
        Assertions.assertEquals("qwe", commentDto.getText());
        Assertions.assertEquals(56, commentDto.getItemId());
        Assertions.assertEquals("Max", commentDto.getAuthorName());
    }
}
