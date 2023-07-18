package ru.practicum.shareit.server.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.exception.EmailException;
import ru.practicum.shareit.server.exception.EmailNullException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.repository.UserRepository;
import ru.practicum.shareit.server.user.util.UserMapper;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl {

    private final UserRepository userRepository;

    public List<UserDto> getUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("All users found");
        return users;
    }

    public UserDto createUser(UserDto user) {
        valid(user.getEmail());
        if (userRepository.findAll().stream()
                .anyMatch(userEmail -> userEmail.getEmail().equals(user.getEmail()))) {
            user.setEmail(null);
            userRepository.save(UserMapper.toUser(user));
            throw new EmailException("Email should be unique");
        }
        User userDto = userRepository.save(UserMapper.toUser(user));
        log.info("User with id = {} created", userDto.getId());
        return UserMapper.toUserDto(userDto);
    }

    public UserDto updateUser(UserDto user, long id) {
        User newUser = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        patch(newUser, UserMapper.toUser(user));
        newUser.setId(id);
        User userDto = userRepository.save(newUser);
        log.info("User with id = {} updated", newUser.getId());
        return UserMapper.toUserDto(userDto);
    }

    public UserDto findUser(long id) {
        User userDto;
        if (userRepository.findById(id).isPresent()) {
            userDto = userRepository.findById(id).get();
        } else {
            throw new NotFoundException(String.format("User with id = %d not found", id));
        }
        log.info("User with id = {} found", id);
        return UserMapper.toUserDto(userDto);
    }

    public void deleteUser(long id) {
        log.info("User with id = {} deleted", id);
        userRepository.deleteById(id);
    }

    private void valid(String email) {
        if (email == null) {
            throw new EmailNullException("Email can't be null");
        }
    }

    private void patch(User oldUser, User newUser) {
        if (userRepository.findAll().stream()
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
    }
}
