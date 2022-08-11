package ru.yandex.practicum.filmorate.storage.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
        return jdbcTemplate.query(sqlQuery, FriendsDbStorage::mapRowToUser, userId);
    }

    public List<User> getCommonListOfFriends(Long userId, Long userOtherId) {
        String sqlQuery = "SELECT u.* FROM users u " +
                "JOIN user_friends f1 ON u.user_id = f1.friend_id " +
                "JOIN user_friends f2 ON u.user_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.query(sqlQuery, FriendsDbStorage::mapRowToUser, userId, userOtherId);
    }

    protected static User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(
                resultSet.getLong("user_id"),
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getString("user_name"),
                resultSet.getDate("birthday").toLocalDate());
    }
}