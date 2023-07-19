package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers();

    UserDto createUser(UserDto user);

    UserDto updateUser(UserDto user, long id);

    UserDto findUser(long id);

    void deleteUser(long id);
}
