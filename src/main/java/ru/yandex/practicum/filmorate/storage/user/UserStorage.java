package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;
import java.util.Optional;

public interface UserStorage {
    Map<Long, User> getUsers();

    void addUserToStorage(User user);

    void deleteUserFromStorage(Long userId);

    void updateUserInStorage(User user);

    Optional<User> getUserById(Long id);
}
