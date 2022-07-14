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
        if (inMemoryUserStorage.getUsers().containsKey(user.getId())) {
            inMemoryUserStorage.updateUserInStorage(user);
        } else {
            throw new UserNotFoundException("Такого пользователя нет");
        }
    }

    public void addUserFriend(Long userId, Long friendId) {
        if (inMemoryUserStorage.getUsers().containsKey(userId) && inMemoryUserStorage.getUsers().containsKey(friendId)) {
            User user = inMemoryUserStorage.getUsers().get(userId);
            user.addFriend(friendId);
            log.info("add friend");
            inMemoryUserStorage.getUsers().put(userId, user);
            user = inMemoryUserStorage.getUsers().get(friendId);
            user.addFriend(userId);
            inMemoryUserStorage.getUsers().put(friendId, user);
        } else {
            throw new UserNotFoundException("Позьзователь не существует");
        }
    }

    public void deleteUserFromFriends(Long userId, Long friendId) {
        if (inMemoryUserStorage.getUsers().containsKey(userId)) {
            inMemoryUserStorage.getUsers().get(userId).getFriends().remove(friendId);
            inMemoryUserStorage.getUsers().get(friendId).getFriends().remove(userId);
        } else {
            throw new UserNotFoundException("Позьзователь не существует");
        }
    }

    public List<User> getListOfFriends(Long userId) {
        List<User> userList = new ArrayList<>();
        for (Long id : inMemoryUserStorage.getUsers().get(userId).getFriends()) {
            if (inMemoryUserStorage.getUsers().containsKey(id)) {
                userList.add(inMemoryUserStorage.getUsers().get(id));
            }
        }
        return userList;
    }

    public List<User> getCommonListOfFriends(Long userId, Long userOtherId) {
        List<User> usersList = new ArrayList<>();
        log.info("start common friends");
        if (inMemoryUserStorage.getUsers().containsKey(userId) &&
                inMemoryUserStorage.getUsers().containsKey(userOtherId)) {
            try {
                for (Long id : inMemoryUserStorage.getUsers().get(userId).getFriends()) {
                    if (inMemoryUserStorage.getUsers().get(userOtherId).getFriends().contains(id)) {
                        usersList.add(inMemoryUserStorage.getUsers().get(id));
                        log.info("add friend");
                    }
                }
            } catch (NullPointerException e) {
                log.info("Нет друзей");
            }
            log.info("Common list of friends finish");
        } else {
            throw new UserNotFoundException("Такого пользователя нет");
        }
        return usersList;
    }
}
