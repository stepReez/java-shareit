package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserRepository {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, long id);

    List<UserDto> getUsers();

    UserDto findUser(long id);

    void deleteUser(long id);
}
