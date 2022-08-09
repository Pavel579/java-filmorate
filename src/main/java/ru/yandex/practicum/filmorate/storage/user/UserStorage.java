package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    //Метод возвращает список всех пользователей из базы данных
    List<User> getUsers();

    //Метод добавляет пользователя в базу данных
    void addUserToStorage(User user);

    //Метод удаляет пользователя из базы данных
    boolean deleteUserFromStorage(Long userId);

    //Метод обновляет пользователя в базе данных
    void updateUserInStorage(User user);

    //Метод возвращает пользователя по его id
    Optional<User> getUserById(Long id);

    //Метод добавляет дружбу для пользователя id = userId c пользователем id = friendId
    void addUserFriend(Long userId, Long friendId);

    //Метод удаляет дружбу с пользователем id = friendId для пользователя id = userId
    boolean deleteUserFromFriends(Long userId, Long friendId);

    //Метод возвращает список всех друзей пользователя id = userId
    List<User> getListOfFriends(Long userId);

    //Метод возвращает список общих друзей пользователей с id = userId и id = userOtherId
    List<User> getCommonListOfFriends(Long userId, Long userOtherId);
}
