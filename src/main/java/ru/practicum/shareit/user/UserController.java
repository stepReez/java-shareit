package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.EmailNullException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ExceptionHandler(EmailNullException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HttpStatus handleBadRequest(final EmailNullException e) {
        return HttpStatus.BAD_REQUEST;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Valid @RequestBody User user, @PathVariable long id) {
        return userService.updateUser(user, id);
    }

    @GetMapping("/{id}")
    public UserDto findUser(@PathVariable long id) {
        return userService.findUser(id);
    }

    @DeleteMapping("/{id}")
    public  void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }
}
