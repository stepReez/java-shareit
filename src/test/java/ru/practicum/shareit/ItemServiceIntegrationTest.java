package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
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
    ItemServiceImpl itemService;

    @Autowired
    UserServiceImpl userService;

    @Test
    public void createItemTest() {
        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("qwe@qwe.com")
                .build();

        UserDto userDto1 = userService.createUser(userDto);

        ItemDtoCreate itemDtoCreate = ItemDtoCreate.builder()
                .name("ItemName")
                .description("Description")
                .available(true)
                .build();

        ItemDto itemDto = itemService.createItem(itemDtoCreate, userDto1.getId());

        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo(itemDtoCreate.getName()));
        assertThat(itemDto.getDescription(), equalTo(itemDtoCreate.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(itemDtoCreate.getAvailable()));
    }

    @Test
    public void patchItemTest() {
        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("qwe@qwe.com")
                .build();

        UserDto userDto1 = userService.createUser(userDto);

        ItemDtoCreate itemDtoCreate = ItemDtoCreate.builder()
                .name("ItemName")
                .description("Description")
                .available(true)
                .build();

        ItemDto itemDto1 = itemService.createItem(itemDtoCreate, userDto1.getId());

        ItemDto itemDtoPatch = ItemDto.builder()
                .name("newName")
                .build();

        ItemDto itemDto = itemService.patchItem(itemDtoPatch, itemDto1.getId(), userDto1.getId());

        assertThat(itemDto.getName(), equalTo(itemDtoPatch.getName()));
        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getDescription(), equalTo(itemDtoCreate.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(itemDtoCreate.getAvailable()));
    }

    @Test
    public void findByUserTest() {
        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("qwe@qwe.com")
                .build();

        UserDto userDto1 = userService.createUser(userDto);

        ItemDtoCreate itemDtoCreate = ItemDtoCreate.builder()
                .name("ItemName")
                .description("Description")
                .available(true)
                .build();

        itemService.createItem(itemDtoCreate, userDto1.getId());

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
        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("qwe@qwe.com")
                .build();

        UserDto userDto1 = userService.createUser(userDto);

        ItemDtoCreate itemDtoCreate = ItemDtoCreate.builder()
                .name("ItemName")
                .description("Description")
                .available(true)
                .build();

        itemService.createItem(itemDtoCreate, userDto1.getId());

        ItemDtoCreate itemDtoCreate1 = ItemDtoCreate.builder()
                .name("Desc")
                .description("Text")
                .available(true)
                .build();

        itemService.createItem(itemDtoCreate1, userDto1.getId());

        List<ItemDto> items = itemService.searchItem("es", 0, 10);

        assertThat(items.size(), equalTo(2));
    }
}
