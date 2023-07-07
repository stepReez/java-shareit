package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.exception.EmailNullException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository mockUserRepository;

    UserServiceImpl userService;

    @BeforeEach
    public void init() {
        userService = new UserServiceImpl(mockUserRepository);
    }

    @Test
    public void createUserTest() {
        Mockito
                .when(mockUserRepository.findAll())
                .thenReturn(List.of());

        Mockito
                .when(mockUserRepository.save(Mockito.any()))
                .thenReturn(User.builder()
                        .id(1)
                        .name("Alex")
                        .email("qwe@qwe.com")
                        .build());

        UserDto userDto = UserDto.builder()
                .name("Alex")
                .email("qwe@qwe.com")
                .build();

        UserDto userDto1 = userService.createUser(userDto);

        Assertions.assertEquals(1, userDto1.getId());
        Assertions.assertEquals("Alex", userDto1.getName());
        Assertions.assertEquals("qwe@qwe.com", userDto1.getEmail());
    }

    @Test
    public void createUserWithNoUniqueEmailTest() {
        Mockito
                .when(mockUserRepository.findAll())
                .thenReturn(List.of(User.builder()
                        .id(1)
                        .name("Alex")
                        .email("qwe@qwe.com")
                        .build()));

        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("qwe@qwe.com")
                .build();

        Assertions.assertThrows(EmailException.class, () -> userService.createUser(userDto));
    }

    @Test
    public void findUserTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(User.builder()
                        .id(1)
                        .name("Alex")
                        .email("qwe@qwe.com")
                        .build()));

        UserDto userDto = userService.findUser(1);

        Assertions.assertEquals(1, userDto.getId());
        Assertions.assertEquals("Alex", userDto.getName());
        Assertions.assertEquals("qwe@qwe.com", userDto.getEmail());
    }

    @Test
    public void findNotExistedUserTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.findUser(1));
    }

    @Test
    public void updateUserTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(User.builder()
                        .id(1)
                        .name("Alex")
                        .email("qwe@qwe.com")
                        .build()));

        Mockito
                .when(mockUserRepository.findAll())
                .thenReturn(List.of(User.builder()
                        .id(1)
                        .name("Alex")
                        .email("qwe@qwe.com")
                        .build()));

        Mockito
                .when(mockUserRepository.save(Mockito.any()))
                .thenReturn(User.builder()
                        .id(1)
                        .name("Max")
                        .email("abc@qwe.ru")
                        .build());

        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("abc@qwe.ru")
                .build();

        UserDto userDtoTest = userService.updateUser(userDto, 1);

        Assertions.assertEquals(1, userDtoTest.getId());
        Assertions.assertEquals("Max", userDtoTest.getName());
        Assertions.assertEquals("abc@qwe.ru", userDtoTest.getEmail());
    }

    @Test
    public void updateNoExistedUserTest() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.findUser(1));
    }

    @Test
    public void updateUserWithNoUniqueEmail() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(User.builder()
                        .id(1)
                        .name("Alex")
                        .email("qwe@qwe.com")
                        .build()));

        Mockito
                .when(mockUserRepository.findAll())
                .thenReturn(List.of(User.builder()
                        .id(1)
                        .name("Alex")
                        .email("qwe@qwe.com")
                        .build(),
                        User.builder()
                                .id(2)
                                .name("Tom")
                                .email("abc@qwe.ru")
                                .build()));

        UserDto userDto = UserDto.builder()
                .name("Max")
                .email("abc@qwe.ru")
                .build();

        Assertions.assertThrows(EmailException.class, () -> userService.updateUser(userDto, 1));
    }

    @Test
    void createUserWithoutEmail() {
        UserDto userDto = UserDto.builder()
                .name("Max")
                .build();

        Assertions.assertThrows(EmailNullException.class, () -> userService.createUser(userDto));
    }
}
