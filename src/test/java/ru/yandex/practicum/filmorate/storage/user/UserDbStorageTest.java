package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.impl.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserDbStorageTest {
    private final UserDbStorage userDbStorage;
    private final FriendsDbStorage friendsDbStorage;
    User user;
    User user2;
    User user3;
    User user4;

    @BeforeEach
    public void beforeEach() {
        user = new User("user1@mail.ru", "user1", "Pavel",
                LocalDate.of(1988, 5, 10));
        user2 = new User("user2@mail.ru", "user2", "Ivan",
                LocalDate.of(1990, 6, 15));
        user3 = new User(2L, "Updateduser2@mail.ru", "user2", "Alex",
                LocalDate.of(1995, 5, 10));
        user4 = new User("user4@mail.ru", "user4", "Anton",
                LocalDate.of(1993, 6, 15));
    }

    @Test
    public void testAddUserToStorage() {
        userDbStorage.addUserToStorage(user);
        Optional<User> userOptional = userDbStorage.getUserById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testGetUserById() {
        userDbStorage.addUserToStorage(user);
        Optional<User> userOptional = userDbStorage.getUserById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testGetAllUsers() {
        userDbStorage.addUserToStorage(user);
        userDbStorage.addUserToStorage(user2);
        List<User> userList = new ArrayList<>(userDbStorage.getUsers());
        Optional<User> userOptional = Optional.of(userList.get(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "user2@mail.ru")
                );
    }

    @Test
    public void testDeleteUserFromStorage() {
        userDbStorage.addUserToStorage(user);
        userDbStorage.addUserToStorage(user2);
        userDbStorage.deleteUserFromStorage(2L);
        List<User> userList = new ArrayList<>(userDbStorage.getUsers());
        assertEquals(userList.size(), 1);
    }

    @Test
    public void testUpdateUserInStorage() {
        userDbStorage.addUserToStorage(user);
        userDbStorage.addUserToStorage(user2);
        userDbStorage.updateUserInStorage(user3);
        List<User> userList = new ArrayList<>(userDbStorage.getUsers());
        Optional<User> userOptional = Optional.of(userList.get(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "Updateduser2@mail.ru")
                );
    }

    @Test
    public void testAddAndGetUserFriend() {
        userDbStorage.addUserToStorage(user);
        userDbStorage.addUserToStorage(user2);
        friendsDbStorage.addUserFriend(user.getId(), user2.getId());
        List<User> userFriends = friendsDbStorage.getListOfFriends(user.getId());
        Optional<User> userOptional = Optional.of(userFriends.get(0));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "user2@mail.ru")
                );
    }

    @Test
    public void testGetCommonListOfFriends() {
        userDbStorage.addUserToStorage(user);
        userDbStorage.addUserToStorage(user2);
        userDbStorage.addUserToStorage(user4);
        friendsDbStorage.addUserFriend(user.getId(), user4.getId());
        friendsDbStorage.addUserFriend(user2.getId(), user4.getId());
        List<User> userCommonFriends = friendsDbStorage.getCommonListOfFriends(user.getId(), user2.getId());
        Optional<User> userOptional = Optional.of(userCommonFriends.get(0));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "user4@mail.ru")
                );
    }
}