package ru.practicum.shareit.server.unitTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.request.repository.RequestRepository;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.repository.BookingRepository;
import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.comment.model.Comment;
import ru.practicum.shareit.server.comment.repository.CommentRepository;
import ru.practicum.shareit.server.exception.ItemBadRequestException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.dto.ItemDtoBooking;
import ru.practicum.shareit.server.item.dto.ItemDtoCreate;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.item.service.ItemServiceImpl;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RequestRepository requestRepository;

    private ItemServiceImpl itemService;

    @BeforeEach
    public void init() {
        itemService = new ItemServiceImpl(itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                requestRepository);
    }

    @Nested
    @DisplayName("Create Tests")
    class CreateTest {
        @Test
        public void createItemTest() {
            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(User.builder()
                            .id(1)
                            .name("Alex")
                            .email("qwe@qwe.com")
                            .build()));

            when(itemRepository.save(any()))
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

            assertEquals(1, itemDto.getId());
            assertEquals(1, itemDto.getOwner());
            assertEquals("ItemName", itemDto.getName());
            assertEquals("Description", itemDto.getDescription());
            assertEquals(true, itemDto.getAvailable());
        }

        @Test
        void createCommentTest() {
            when(bookingRepository.getBookingByItemAndUser(anyLong(), anyLong()))
                    .thenReturn(List.of(new Booking()));

            when(userRepository.findById(anyLong()))
                    .thenReturn(Optional.of(User.builder().name("Max").build()));

            when(itemRepository.findById(anyLong()))
                    .thenReturn(Optional.of(Item.builder().id(56).build()));

            Comment comment = Comment.builder()
                    .id(1)
                    .text("qwe")
                    .item(Item.builder().id(56).build())
                    .author(User.builder().name("Max").build())
                    .created(LocalDateTime.now().plusDays(1))
                    .build();

            when(commentRepository.save(any()))
                    .thenReturn(comment);

            CommentDto commentDto = CommentDto.builder()
                    .id(1)
                    .text("qwe")
                    .itemId(56)
                    .authorName("Max")
                    .created(LocalDateTime.now().plusDays(1))
                    .build();

            CommentDto commentDto1 = itemService.createComment(commentDto, 56, 1);

            assertEquals(1, commentDto1.getId());
            assertEquals("qwe", commentDto1.getText());
            assertEquals(56, commentDto1.getItemId());
            assertEquals("Max", commentDto1.getAuthorName());
        }

        @Test
        public void createCommentUserWithNoBookTest() {
            CommentDto commentDto = CommentDto.builder()
                    .text("Not Blank Comment")
                    .itemId(1)
                    .authorName("Max")
                    .created(LocalDateTime.now())
                    .build();
            assertThrows(ItemBadRequestException.class,
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

            when(bookingRepository.getBookingByItemAndUser(anyLong(), anyLong()))
                    .thenReturn(List.of(Booking.builder()
                            .build()));

            assertThrows(ItemBadRequestException.class,
                    () -> itemService.createComment(commentDto, 1, 1));
        }

        @Test
        public void itemCreateWithWrongUser() {
            ItemDtoCreate itemDtoCreate = ItemDtoCreate.builder()
                    .name("ItemName")
                    .description("Description")
                    .available(true)
                    .build();

            assertThrows(NotFoundException.class, () -> itemService.createItem(itemDtoCreate, 1));
        }
    }

    @Nested
    @DisplayName("Read Tests")
    class ReadTest {
        @Test
        public void findNotExistingItemTest() {
            assertThrows(NotFoundException.class, () -> itemService.findItem(1,1));
        }

        @Test
        void findItemTest() {
            Item item = Item.builder()
                    .id(1)
                    .name("name")
                    .description("description")
                    .available(true)
                    .owner(1)
                    .build();

            when(itemRepository.findById(anyLong()))
                    .thenReturn(Optional.of(item));

            when(bookingRepository.getLastBookingByItemId(anyLong(), any()))
                    .thenReturn(List.of(Booking.builder()
                            .id(1)
                            .item(item)
                            .booker(User.builder().id(2).build())
                            .build()));

            when(bookingRepository.getNextBookingByItemId(anyLong(), any()))
                    .thenReturn(List.of(Booking.builder()
                            .id(2)
                            .item(item)
                            .booker(User.builder().id(2).build())
                            .build()));

            when(commentRepository.findByItemId(anyLong()))
                    .thenReturn(List.of());

            ItemDtoBooking itemDtoBooking =  itemService.findItem(1, 1);

            ItemDtoBooking itemDtoBookingExpected = ItemDtoBooking.builder()
                    .id(1)
                    .name("name")
                    .description("description")
                    .available(true)
                    .owner(1)
                    .lastBooking(BookingDto.builder().id(1).build())
                    .nextBooking(BookingDto.builder().id(2).build())
                    .comments(List.of())
                    .build();

            assertEquals(itemDtoBookingExpected.getId(), itemDtoBooking.getId());
            assertEquals(itemDtoBookingExpected.getName(), itemDtoBooking.getName());
            assertEquals(itemDtoBookingExpected.getDescription(), itemDtoBooking.getDescription());
            assertEquals(itemDtoBookingExpected.getAvailable(), itemDtoBooking.getAvailable());
            assertEquals(itemDtoBookingExpected.getOwner(), itemDtoBooking.getOwner());
            assertEquals(itemDtoBookingExpected.getLastBooking().getId(),
                    itemDtoBooking.getLastBooking().getId());
            assertEquals(itemDtoBookingExpected.getNextBooking().getId(),
                    itemDtoBooking.getNextBooking().getId());
        }
    }

    @Nested
    @DisplayName("Update test")
    class UpdateTest {
        @Test
        public void itemUpdateNotFoundTest() {
            ItemDto itemDto = ItemDto.builder()
                    .name("New Name")
                    .build();

            assertThrows(NotFoundException.class, () -> itemService.patchItem(itemDto, 1, 2));
        }

        @Test
        public void itemUpdateWithWrongUserTest() {
            when(itemRepository.findById(anyLong()))
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

            assertThrows(NotFoundException.class, () -> itemService.patchItem(itemDto, 1, 2));
        }
    }
}
