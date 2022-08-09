package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public void addUserToStorage(User user) {
        String sqlQuery = "INSERT INTO users (email, login, user_name, birthday) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public boolean deleteUserFromStorage(Long userId) {
        String sqlQuery = "DELETE FROM users WHERE user_id = ?";
        return jdbcTemplate.update(sqlQuery, userId) > 0;
    }

    @Override
    public void updateUserInStorage(User user) {
        String sqlQuery = "UPDATE users SET " +
                "email = ?, login = ?, user_name = ?, birthday = ? " +
                "WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
    }

    @Override
    public Optional<User> getUserById(Long id) {
        String sqlQuery = "SELECT user_id, email, login, user_name, birthday " +
                "FROM users WHERE user_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id).stream().findAny();
    }

    public void addUserFriend(Long userId, Long friendId) {
        String sqlQuery = "INSERT INTO user_friends (user_id, friend_id) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    public boolean deleteUserFromFriends(Long userId, Long friendId) {
        String sqlQuery = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.update(sqlQuery, userId, friendId) > 0;
    }

    public List<User> getListOfFriends(Long userId) {
        String sqlQuery = "SELECT u.* FROM users u " +
                "WHERE u.user_id IN (SELECT friend_id " +
                "FROM user_friends " +
                "WHERE user_id = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
    }

    public List<User> getCommonListOfFriends(Long userId, Long userOtherId) {
        List<User> userFriendList = getListOfFriends(userId);
        List<User> otherUserFriendList = getListOfFriends(userOtherId);
        List<User> commonListOfFriends = new ArrayList<>();
        for (User user : userFriendList) {
            if (otherUserFriendList.contains(user)) {
                commonListOfFriends.add(user);
            }
        }
        return commonListOfFriends;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(
                resultSet.getLong("user_id"),
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getString("user_name"),
                resultSet.getDate("birthday").toLocalDate());
    }
}
