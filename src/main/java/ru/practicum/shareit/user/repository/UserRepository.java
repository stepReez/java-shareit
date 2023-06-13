package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    UserDto createUser(User user);

    UserDto updateUser(User user, long id);

    List<UserDto> getUsers();

    UserDto findUser(long id);

    void deleteUser(long id);
}
