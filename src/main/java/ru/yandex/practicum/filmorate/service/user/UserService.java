package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage inMemoryUserStorage;

    @Autowired
    public UserService(UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(inMemoryUserStorage.getUsers().values());
    }

    public User getUserById(Long id) {
        return inMemoryUserStorage.getUserById(id).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    public void addUserToStorage(User user) {
        inMemoryUserStorage.addUserToStorage(user);
    }

    public void updateUserInStorage(User user) {
        getUserById(user.getId());
        inMemoryUserStorage.updateUserInStorage(user);
    }

    public void addUserFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User userFriend = getUserById(friendId);
        user.addFriend(friendId);
        log.info("add friend");
        inMemoryUserStorage.getUsers().put(userId, user);
        userFriend.addFriend(userId);
        inMemoryUserStorage.getUsers().put(friendId, userFriend);
    }

    public void deleteUserFromFriends(Long userId, Long friendId) {
        getUserById(userId).getFriends().remove(friendId);
        getUserById(friendId).getFriends().remove(userId);
    }

    public List<User> getListOfFriends(Long userId) {
        List<User> userList = new ArrayList<>();
        for (Long id : getUserById(userId).getFriends()) {
            if (inMemoryUserStorage.getUsers().containsKey(id)) {
                userList.add(getUserById(id));
            }
        }
        return userList;
    }

    public List<User> getCommonListOfFriends(Long userId, Long userOtherId) {
        List<User> usersList = new ArrayList<>();
        log.info("start common friends");
        if (getUserById(userId).getFriends() != null && getUserById(userOtherId).getFriends() != null){
            for (Long id : getUserById(userId).getFriends()) {
                if (getUserById(userOtherId).getFriends().contains(id)) {
                    usersList.add(getUserById(id));
                    log.info("add friend");
                }
            }
        }
        return usersList;
    }
}
