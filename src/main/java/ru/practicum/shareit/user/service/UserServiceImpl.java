package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.exception.EmailNullException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.util.UserMapper;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl {
    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

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
        User userDto = userRepository.save(UserMapper.toUser(user));
        log.info("User with id = {} created", userDto.getId());
        return UserMapper.toUserDto(userDto);
    }

    public UserDto updateUser(UserDto user, long id) {
        user.setId(id);
        User userDto = userRepository.save(UserMapper.toUser(user));
        log.info("User with id = {} updated", userDto.getId());
        return UserMapper.toUserDto(userDto);
    }

    public UserDto findUser(long id) {
        User userDto = userRepository.findById(id).get();
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
        if (userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email))) {
            throw new EmailException("Email should be unique");
        }
    }
}
