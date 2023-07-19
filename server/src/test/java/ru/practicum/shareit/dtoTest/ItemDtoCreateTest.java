package ru.practicum.shareit.dtoTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDtoCreate;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoCreateTest {

    @Autowired
    private JacksonTester<ItemDtoCreate> json;

    @Test
    void testItemDtoCreate() throws Exception {
        ItemDtoCreate itemDtoCreate = ItemDtoCreate.builder()
                .name("Name")
                .description("Description")
                .available(true)
                .requestId(1)
                .build();

        JsonContent<ItemDtoCreate> result = json.write(itemDtoCreate);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}
