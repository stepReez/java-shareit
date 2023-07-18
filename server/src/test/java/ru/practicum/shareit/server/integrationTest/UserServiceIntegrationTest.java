package ru.practicum.shareit.server.integrationTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
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
public class UserServiceIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    private final UserDto userDto = UserDto.builder()
            .name("Max")
            .email("qwe@qwe.com")
            .build();

    private UserDto user;

    @BeforeEach
    public void init() {
        user = userService.createUser(userDto);
    }

    @Test
    public void createUserTest() {
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void updateUserTest() {
       UserDto userDto1 = userService.updateUser(UserDto.builder().name("Pavel").build(), user.getId());

        assertThat(userDto1.getId(), notNullValue());
        assertThat(userDto1.getName(), equalTo("Pavel"));
        assertThat(userDto1.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void findUserTest() {
        UserDto userDto1 = userService.findUser(user.getId());

        assertThat(userDto1.getId(), notNullValue());
        assertThat(userDto1.getName(), equalTo(userDto.getName()));
        assertThat(userDto1.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void getUsersTest() {
        UserDto userDto1 = UserDto.builder()
                .name("Not Max")
                .email("q@q.q")
                .build();

        userService.createUser(userDto1);

        List<UserDto> users = userService.getUsers();

        assertThat(users.size(), equalTo(2));
    }

    @Test
    void deleteUserTest() {
        userService.deleteUser(user.getId());
    }
}
