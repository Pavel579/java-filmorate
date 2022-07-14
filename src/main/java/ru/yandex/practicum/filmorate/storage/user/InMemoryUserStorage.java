package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long userId = 0L;

    public Map<Long, User> getUsers() {
        return new HashMap<>(users);
    }

    @Override
    public void addUserToStorage(User user) {
        user.setId(++userId);
        users.put(user.getId(), user);
    }

    @Override
    public void deleteUserFromStorage(Long userId) {
        users.remove(userId);
    }

    @Override
    public void updateUserInStorage(User user) {
        users.replace(user.getId(), user);
    }

    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
}
