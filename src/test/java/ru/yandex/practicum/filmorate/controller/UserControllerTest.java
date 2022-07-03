package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    UserController userController;
    User user;

    @BeforeEach
    void beforeEach() {
        user = new User(1, "mail@mail.ru", "login", "Pavel", LocalDate.of(1988, 5, 10));
        userController = new UserController();
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
