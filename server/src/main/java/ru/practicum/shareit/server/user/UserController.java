package ru.practicum.shareit.server.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserServiceImpl;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto user) {
        return userService.createUser(user);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody UserDto user, @PathVariable long id) {
        return userService.updateUser(user, id);
    }

    @GetMapping("/{id}")
    public UserDto findUser(@PathVariable long id) {
        return userService.findUser(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }
}
