package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    Map<Integer, User> users = new HashMap<>();
    private Integer userId = 0;

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User saveUser(@Valid @RequestBody User user) {
        log.info("create user");
        validateUser(user);
        log.debug("user passed validation");
        user.setId(++userId);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("update user");
        validateUser(user);
        log.debug("user passed validation");
        if (users.containsKey(user.getId())) {
            users.replace(user.getId(), user);
        } else {
            throw new RuntimeException("Такого пользователя нет");
        }
        return user;
    }

    protected void validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            throw new RuntimeException("Логин не должен содержать пробелы");
        }
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

}
