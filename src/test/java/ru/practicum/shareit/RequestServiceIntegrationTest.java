package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestServiceImpl;
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
public class RequestServiceIntegrationTest {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    RequestServiceImpl requestService;

    @Test
    public void createRequestTest() {
        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("qwe@qwe.com")
                .build();

        UserDto userDto1 = userService.createUser(userDto);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("description")
                .requesterId(userDto.getId())
                .build();

        ItemRequestDto itemRequestDto1 = requestService.createRequest(itemRequestDto, userDto1.getId());

        assertThat(itemRequestDto1.getId(), notNullValue());
        assertThat(itemRequestDto1.getRequesterId(), equalTo(userDto1.getId()));
        assertThat(itemRequestDto1.getDescription(), equalTo(itemRequestDto.getDescription()));
    }

    @Test
    public void getUserRequestsTest() {
        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("qwe@qwe.com")
                .build();

        UserDto userDto1 = userService.createUser(userDto);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("description")
                .requesterId(userDto.getId())
                .build();

        ItemRequestDto itemRequestDto1 = requestService.createRequest(itemRequestDto, userDto1.getId());

        List<ItemRequestDto> requests = requestService.getUserRequests(userDto1.getId());

        assertThat(requests.size(), equalTo(1));
    }

    @Test
    public void getOtherUsersRequestsTest() {
        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("qwe@qwe.com")
                .build();

        UserDto userDto1 = userService.createUser(userDto);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("description")
                .requesterId(userDto.getId())
                .build();

        ItemRequestDto itemRequestDto1 = requestService.createRequest(itemRequestDto, userDto1.getId());

        List<ItemRequestDto> requests = requestService.getOtherUsersRequests(userDto1.getId(), 0, 10);

        assertThat(requests.size(), equalTo(0));
    }

    @Test
    public void getOneRequestTest() {
        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("qwe@qwe.com")
                .build();

        UserDto userDto1 = userService.createUser(userDto);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("description")
                .requesterId(userDto.getId())
                .build();

        ItemRequestDto itemRequestDtoWithId = requestService.createRequest(itemRequestDto, userDto1.getId());

        ItemRequestDto itemRequestDto1 = requestService.getOneRequest(itemRequestDtoWithId.getId(), userDto1.getId());

                assertThat(itemRequestDto1.getId(), notNullValue());
        assertThat(itemRequestDto1.getRequesterId(), equalTo(userDto1.getId()));
        assertThat(itemRequestDto1.getDescription(), equalTo(itemRequestDto.getDescription()));
    }
}
