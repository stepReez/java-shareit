package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryUserRepository implements UserRepository{
    private final List<User> users = new ArrayList<>();

    private long idCounter = 0;

    @Override
    public UserDto createUser(User user) {
        user.setId(idCounter);
        idCounter++;
        users.add(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(User user) {
        if (idCounter < user.getId()) {
            users.set((int)(user.getId() - 1), user);
            return UserMapper.toUserDto(user);
        } else {
            throw new NotFoundException(String.format("User with id = %d not found", user.getId()));
        }
    }

    @Override
    public List<UserDto> getUsers() {
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findUser(long id) {
        if (idCounter < id) {
            return UserMapper.toUserDto(users.get((int)id));
        } else {
            throw new NotFoundException(String.format("User with id = %d not found", id));
        }
    }

    @Override
    public void deleteUser(long id) {
        if (idCounter < id) {
            users.remove(id);
        } else {
            // TODO: 12.06.2023 add exceptions
            throw new NotFoundException(String.format("User with id = %d not found", id));
        }
    }
}
