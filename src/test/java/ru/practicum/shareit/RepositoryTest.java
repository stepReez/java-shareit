package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class RepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void findItemByRequestTest() {
        User user = User.builder()
                .email("qwe@qwe.com")
                .name("Max")
                .build();

        userRepository.save(user);

        User user1 = User.builder()
                .email("qwe@qwq.com")
                .name("Alex")
                .build();

        userRepository.save(user1);

        ItemRequest itemRequest = ItemRequest.builder()
                .requester(user1)
                .description("123")
                .created(LocalDateTime.now())
                .build();

        requestRepository.save(itemRequest);

        Item item = Item.builder()
                .name("Name")
                .owner(user.getId())
                .description("Description")
                .request(itemRequest)
                .available(true)
                .build();

        itemRepository.save(item);

        List<Item> items = itemRepository.findByRequest(itemRequest.getId());

        Assertions.assertEquals(1, items.size());

        Item item1 = items.get(0);

        Assertions.assertEquals("Name", item1.getName());
        Assertions.assertEquals("Description", item1.getDescription());
        Assertions.assertEquals(user.getId(), item1.getOwner());
        Assertions.assertEquals(true, item1.getAvailable());
    }

    @Test
    void findByStateWaitingByBookerTest() {
        User user = User.builder()
                .email("qwe@qwe.com")
                .name("Max")
                .build();

        userRepository.save(user);

        User user1 = User.builder()
                .email("qwe@qwq.com")
                .name("Alex")
                .build();

        userRepository.save(user1);

        Item item = Item.builder()
                .name("Name")
                .owner(user.getId())
                .description("Description")
                .available(true)
                .build();

        itemRepository.save(item);

        Booking booking = Booking.builder()
                .booker(user1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();

        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByStateWaitingByBooker(user1.getId(),
                PageRequest.of(0, 10));

        Assertions.assertEquals(1, bookings.size());
    }

    @Test
    void findAllByOwner() {
        User user = User.builder()
                .email("qwe@qwe.com")
                .name("Max")
                .build();

        userRepository.save(user);

        User user1 = User.builder()
                .email("qwe@qwq.com")
                .name("Alex")
                .build();

        userRepository.save(user1);

        Item item = Item.builder()
                .name("Name")
                .owner(user.getId())
                .description("Description")
                .available(true)
                .build();

        itemRepository.save(item);

        Booking booking = Booking.builder()
                .booker(user1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .status(BookingStatus.WAITING)
                .build();

        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByStateWaitingByBooker(user1.getId(), PageRequest.of(0, 10));

        Assertions.assertEquals(1, bookings.size());
    }
}
