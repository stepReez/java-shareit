package ru.practicum.shareit.repositoryTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private final User user = User.builder()
            .email("qwe@qwe.com")
            .name("Max")
            .build();

    private final User user1 = User.builder()
            .email("qwe@qwq.com")
            .name("Alex")
            .build();

    @BeforeEach
    public void init() {
        userRepository.save(user);

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
    }


    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void findByStateWaitingByBookerTest() {
        List<Booking> bookings = bookingRepository.findByStateWaitingByBooker(user1.getId(),
                PageRequest.of(0, 10));

        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByOwner() {
        List<Booking> bookings = bookingRepository.findByStateWaitingByBooker(user1.getId(), PageRequest.of(0, 10));

        assertEquals(1, bookings.size());
    }
}
