package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    UserController userController;
    User user;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "mail@mail.ru", "login", "Pavel", LocalDate.of(1988, 5, 10), new HashSet<>());
        userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    void createUser() {
        userController.saveUser(user);
        assertEquals(user.getName(), userController.getAllUsers().get(0).getName());
    }

    @Test
    void createUserWithoutName() {
        user.setName("");
        userController.saveUser(user);
        assertEquals(user.getName(), userController.getAllUsers().get(0).getLogin());
    }

    @Test
    void createUserWithIncorrectLogin() {
        user.setLogin("login login");
        Throwable exception = assertThrows(RuntimeException.class, () -> userController.saveUser(user));
        assertEquals("Логин не должен содержать пробелы", exception.getMessage());
    }
}
