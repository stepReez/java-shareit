package ru.practicum.shareit.unitTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository mockUserRepository;

    private UserServiceImpl userService;

    @BeforeEach
    public void init() {
        userService = new UserServiceImpl(mockUserRepository);
    }

    @Nested
    @DisplayName("Create Test")
    class CreateTest {
        @Test
        public void createUserTest() {
            when(mockUserRepository.findAll())
                    .thenReturn(List.of());

            when(mockUserRepository.save(any()))
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

            assertEquals(1, userDto1.getId());
            assertEquals("Alex", userDto1.getName());
            assertEquals("qwe@qwe.com", userDto1.getEmail());
        }

        @Test
        public void createUserWithNoUniqueEmailTest() {
            when(mockUserRepository.findAll())
                    .thenReturn(List.of(User.builder()
                            .id(1)
                            .name("Alex")
                            .email("qwe@qwe.com")
                            .build()));

            UserDto userDto = UserDto.builder()
                    .name("Max")
                    .email("qwe@qwe.com")
                    .build();

            assertThrows(EmailException.class, () -> userService.createUser(userDto));
        }

        @Test
        void createUserWithoutEmail() {
            UserDto userDto = UserDto.builder()
                    .name("Max")
                    .build();

            assertThrows(EmailNullException.class, () -> userService.createUser(userDto));
        }
    }

    @Nested
    @DisplayName("Read Test")
    class ReadTest {
        @Test
        public void findUserTest() {
            when(mockUserRepository.findById(anyLong()))
                    .thenReturn(Optional.of(User.builder()
                            .id(1)
                            .name("Alex")
                            .email("qwe@qwe.com")
                            .build()));

            UserDto userDto = userService.findUser(1);

            assertEquals(1, userDto.getId());
            assertEquals("Alex", userDto.getName());
            assertEquals("qwe@qwe.com", userDto.getEmail());
        }

        @Test
        public void findNotExistedUserTest() {
            when(mockUserRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> userService.findUser(1));
        }
    }

    @Nested
    @DisplayName("Update Test")
    class UpdateTest {
        @Test
        public void updateUserTest() {
            when(mockUserRepository.findById(anyLong()))
                    .thenReturn(Optional.of(User.builder()
                            .id(1)
                            .name("Alex")
                            .email("qwe@qwe.com")
                            .build()));

            when(mockUserRepository.findAll())
                    .thenReturn(List.of(User.builder()
                            .id(1)
                            .name("Alex")
                            .email("qwe@qwe.com")
                            .build()));

            when(mockUserRepository.save(any()))
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

            assertEquals(1, userDtoTest.getId());
            assertEquals("Max", userDtoTest.getName());
            assertEquals("abc@qwe.ru", userDtoTest.getEmail());
        }

        @Test
        public void updateNoExistedUserTest() {
            when(mockUserRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> userService.findUser(1));
        }

        @Test
        public void updateUserWithNoUniqueEmail() {
            when(mockUserRepository.findById(anyLong()))
                    .thenReturn(Optional.of(User.builder()
                            .id(1)
                            .name("Alex")
                            .email("qwe@qwe.com")
                            .build()));

            when(mockUserRepository.findAll())
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

            assertThrows(EmailException.class, () -> userService.updateUser(userDto, 1));
        }
    }
}
