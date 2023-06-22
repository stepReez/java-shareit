package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.exception.EmailNullException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.util.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryUserRepository {
    private final Map<Long, User> users = new HashMap<>();

    private long idCounter = 1;

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        valid(user.getEmail());
        user.setId(idCounter);
        users.put(idCounter, user);
        idCounter++;
        return UserMapper.toUserDto(user);
    }

    public UserDto updateUser(UserDto userDto, long id) {
        User user = UserMapper.toUser(userDto);
        if (idCounter > id) {
            User oldUser = users.get(id);
            user = patch(oldUser, user);
            users.put(id, user);
            return UserMapper.toUserDto(user);
        } else {
            throw new NotFoundException(String.format("User with id = %d not found", id));
        }
    }

    public List<UserDto> getUsers() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto findUser(long id) {
        if (idCounter > id) {
            return UserMapper.toUserDto(users.get(id));
        } else {
            throw new NotFoundException(String.format("User with id = %d not found", id));
        }
    }

    public void deleteUser(long id) {
        if (idCounter > id) {
            users.remove(id);
        } else {
            throw new NotFoundException(String.format("User with id = %d not found", id));
        }
    }

    private void valid(String email) {
        if (email == null) {
            throw new EmailNullException("Email can't be null");
        }
        if (users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email))) {
                throw new EmailException("Email should be unique");
        }
    }

    private User patch(User oldUser, User newUser) {
        if (users.values().stream()
                .anyMatch(user -> user.getEmail().equals(newUser.getEmail()) &&
                !oldUser.getEmail().equals(newUser.getEmail()))) {
            throw new EmailException("Email should be unique");
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
