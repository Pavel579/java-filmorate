package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage UserDbStorage;

    @Autowired
    public UserService(UserStorage UserDbStorage) {
        this.UserDbStorage = UserDbStorage;
    }

    public List<User> getAllUsers() {
        return UserDbStorage.getUsers();
    }

    public User getUserById(Long id) {
        return UserDbStorage.getUserById(id).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    public void addUserToStorage(User user) {
        UserDbStorage.addUserToStorage(user);
    }

    public void updateUserInStorage(User user) {
        getUserById(user.getId());
        UserDbStorage.updateUserInStorage(user);
    }

    public void addUserFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);
        UserDbStorage.addUserFriend(userId, friendId);
    }

    public boolean deleteUserFromFriends(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);
        return UserDbStorage.deleteUserFromFriends(userId, friendId);
    }

    public List<User> getListOfFriends(Long userId) {
        getUserById(userId);
        return UserDbStorage.getListOfFriends(userId);
    }

    public List<User> getCommonListOfFriends(Long userId, Long userOtherId) {
        return UserDbStorage.getCommonListOfFriends(userId, userOtherId);
    }
}
