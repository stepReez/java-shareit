package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

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
    UserServiceImpl userService;

    @Test
    public void createUserTest() {
        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("qwe@qwe.com")
                .build();

        UserDto user = userService.createUser(userDto);

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void updateUserTest() {
        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("qwe@qwe.com")
                .build();

        UserDto userDto1 = userService.createUser(userDto);

        UserDto user = userService.updateUser(UserDto.builder().name("Pavel").build(), userDto1.getId());

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo("Pavel"));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void findUserTest() {
        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("qwe@qwe.com")
                .build();

        UserDto userDto1 = userService.createUser(userDto);

        UserDto user = userService.findUser(userDto1.getId());

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }
}
