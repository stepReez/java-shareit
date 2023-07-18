package ru.practicum.shareit.server.integrationTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.service.RequestServiceImpl;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserServiceImpl;

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
    private UserServiceImpl userService;

    @Autowired
    private RequestServiceImpl requestService;

    private final UserDto userDto = UserDto.builder()
            .name("Max")
            .email("qwe@qwe.com")
            .build();

    private UserDto userDto1;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    public void init() {
        userDto1 = userService.createUser(userDto);

        itemRequestDto = ItemRequestDto.builder()
                .description("description")
                .requesterId(userDto.getId())
                .build();
    }

    @Test
    public void createRequestTest() {
        ItemRequestDto itemRequestDto1 = requestService.createRequest(itemRequestDto, userDto1.getId());

        test(itemRequestDto1);
    }

    @Test
    public void getUserRequestsTest() {
        requestService.createRequest(itemRequestDto, userDto1.getId());

        List<ItemRequestDto> requests = requestService.getUserRequests(userDto1.getId());

        assertThat(requests.size(), equalTo(1));
    }

    @Test
    public void getOtherUsersRequestsTest() {
        requestService.createRequest(itemRequestDto, userDto1.getId());

        List<ItemRequestDto> requests = requestService.getOtherUsersRequests(userDto1.getId(), 0, 10);

        assertThat(requests.size(), equalTo(0));
    }

    @Test
    public void getOneRequestTest() {
        ItemRequestDto itemRequestDtoWithId = requestService.createRequest(itemRequestDto, userDto1.getId());

        ItemRequestDto itemRequestDto1 = requestService.getOneRequest(itemRequestDtoWithId.getId(), userDto1.getId());

        test(itemRequestDto1);
    }

    private void test(ItemRequestDto itemRequestDtoTest) {
        assertThat(itemRequestDtoTest.getId(), notNullValue());
        assertThat(itemRequestDtoTest.getRequesterId(), equalTo(userDto1.getId()));
        assertThat(itemRequestDtoTest.getDescription(), equalTo(itemRequestDto.getDescription()));
    }
}
