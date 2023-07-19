package ru.practicum.shareit.integrationTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserServiceImpl userService;

    private final UserDto userDto = UserDto.builder()
            .name("Max")
            .email("qwe@qwe.com")
            .build();

    private final ItemDtoCreate itemDtoCreate = ItemDtoCreate.builder()
            .name("ItemName")
            .description("Description")
            .available(true)
            .build();

    private UserDto userDto1;

    private ItemDto itemDto;

    @BeforeEach
    public void init() {
        userDto1 = userService.createUser(userDto);

        itemDto = itemService.createItem(itemDtoCreate, userDto1.getId());
    }

    @Test
    public void createItemTest() {
        test(itemDto);
        assertThat(itemDto.getName(), equalTo(itemDtoCreate.getName()));
    }

    @Test
    public void patchItemTest() {
        ItemDto itemDtoPatch = ItemDto.builder()
                .name("newName")
                .build();

        ItemDto itemDto1 = itemService.patchItem(itemDtoPatch, itemDto.getId(), userDto1.getId());
        test(itemDto1);
        assertThat(itemDto1.getName(), equalTo(itemDtoPatch.getName()));
    }

    @Test
    public void findByUserTest() {
        ItemDtoCreate itemDtoCreate1 = ItemDtoCreate.builder()
                .name("ItemName2")
                .description("Description2")
                .available(true)
                .build();

        itemService.createItem(itemDtoCreate1, userDto1.getId());

        List<ItemDtoBooking> items = itemService.findItemsByUser(userDto1.getId(), 0, 10);

        assertThat(items.size(), equalTo(2));
    }

    @Test
    public void searchItem() {
        ItemDtoCreate itemDtoCreate1 = ItemDtoCreate.builder()
                .name("Desc")
                .description("Text")
                .available(true)
                .build();

        itemService.createItem(itemDtoCreate1, userDto1.getId());

        List<ItemDto> items = itemService.searchItem("es", 0, 10);

        assertThat(items.size(), equalTo(2));
    }

    private void test(ItemDto itemDto) {
        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getDescription(), equalTo(itemDtoCreate.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(itemDtoCreate.getAvailable()));
    }
}
