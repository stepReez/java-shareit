package ru.practicum.shareit.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl {
    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<UserDto> getUsers() {
        List<UserDto> users = userRepository.getUsers();
        log.info("All users found");
        return users;
    }

    public UserDto createUser(UserDto user) {
        UserDto userDto = userRepository.createUser(user);
        log.info("User with id = {} created", userDto.getId());
        return userDto;
    }

    public UserDto updateUser(UserDto user, long id) {
        UserDto userDto = userRepository.updateUser(user, id);
        log.info("User with id = {} updated", userDto.getId());
        return userDto;
    }

    public UserDto findUser(long id) {
        UserDto userDto = userRepository.findUser(id);
        log.info("User with id = {} found", id);
        return userDto;
    }

    public void deleteUser(long id) {
        log.info("User with id = {} deleted", id);
        userRepository.deleteUser(id);
    }
}
