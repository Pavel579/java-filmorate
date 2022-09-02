package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userDbStorage;
    private final FriendsStorage friendsDbStorage;

    @Autowired
    public UserService(UserStorage UserDbStorage, FriendsStorage friendsDbStorage) {
        this.userDbStorage = UserDbStorage;
        this.friendsDbStorage = friendsDbStorage;
    }

    public List<User> getAllUsers() {
        return userDbStorage.getUsers();
    }

    public User getUserById(Long id) {
        return userDbStorage.getUserById(id).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    public void addUserToStorage(User user) {
        userDbStorage.addUserToStorage(user);
    }

    public void updateUserInStorage(User user) {
        getUserById(user.getId());
        userDbStorage.updateUserInStorage(user);
    }

    public void addUserFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);
        friendsDbStorage.addUserFriend(userId, friendId);
    }

    public void deleteUserFromFriends(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);
        friendsDbStorage.deleteUserFromFriends(userId, friendId);
    }

    public List<User> getListOfFriends(Long userId) {
        getUserById(userId);
        return friendsDbStorage.getListOfFriends(userId);
    }

    public List<User> getCommonListOfFriends(Long userId, Long userOtherId) {
        return friendsDbStorage.getCommonListOfFriends(userId, userOtherId);
    }

    public void removeUserById(Long userId) {
        getUserById(userId);
        userDbStorage.removeUserById(userId);
    }
}
