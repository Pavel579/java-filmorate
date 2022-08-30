package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> getDirectors() {
        String sqlQuery = "SELECT * FROM directors";
        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    @Override
    public Optional<Director> getDirectorById(int id) {
        String sqlQuery = "SELECT * FROM directors WHERE id=?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector, id).stream().findAny();
    }

    @Override
    public Director createDirector(Director director) {
        String sqlQuery = "INSERT INTO directors (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sqlQuery = "UPDATE directors SET name = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(int id) {
        String sqlQuery = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return new Director(
                resultSet.getInt("id"),
                resultSet.getString("name"));
    }

}
