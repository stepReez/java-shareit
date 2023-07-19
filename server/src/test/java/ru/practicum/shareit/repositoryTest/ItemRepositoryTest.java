package ru.practicum.shareit.repositoryTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

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

        assertEquals(1, items.size());

        Item item1 = items.get(0);

        assertEquals("Name", item1.getName());
        assertEquals("Description", item1.getDescription());
        assertEquals(user.getId(), item1.getOwner());
        assertEquals(true, item1.getAvailable());
    }
}