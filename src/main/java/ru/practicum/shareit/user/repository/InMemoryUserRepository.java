package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.exception.EmailNullException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryUserRepository implements UserRepository {
    private final List<User> users = new ArrayList<>();

    private long idCounter = 1;

    @Override
    public UserDto createUser(User user) {
        valid(user.getEmail());
        user.setId(idCounter);
        idCounter++;
        users.add(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(User user, long id) {
        if (idCounter > id) {
            User oldUser = users.get((int)id - 1);
            user = patch(oldUser, user);
            users.set(((int)id - 1), user);
            return UserMapper.toUserDto(user);
        } else {
            throw new NotFoundException(String.format("User with id = %d not found", id));
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
        if (idCounter > id) {
            return UserMapper.toUserDto(users.get((int)id - 1));
        } else {
            throw new NotFoundException(String.format("User with id = %d not found", id));
        }
    }

    @Override
    public void deleteUser(long id) {
        if (idCounter > id) {
            users.remove((int)id - 1);
        } else {
            throw new NotFoundException(String.format("User with id = %d not found", id));
        }
    }

    private void valid(String email) {
        if (email == null) {
            throw new EmailNullException("Email can't be null");
        }
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                throw new EmailException("Email should be unique");
            }
        }
    }

    private User patch(User oldUser, User newUser) {
        for (User user : users) {
            if (user.getEmail().equals(newUser.getEmail()) && !oldUser.getEmail().equals(newUser.getEmail())) {
                throw new EmailException("Email should be unique");
            }
        }
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        return oldUser;
    }
}
