package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT * FROM films f JOIN mpa m ON f.MPA_ID = m.ID";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film addFilmToStorage(Film film) {
        String sqlQuery = "INSERT INTO films (film_name, film_description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        if (film.getGenres() != null) {
            film.setGenres(addGenresToTable(film));
        }
        addDirectorToTable(film);
        return film;
    }

    @Override
    public void removeFilmById(Long filmId) {
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public Film updateFilmInStorage(Film film) {
        String sqlQuery = "UPDATE films SET " +
                "film_name = ?, film_description = ?, release_date = ?, " +
                "duration = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        String sqlQuery2 = "DELETE FROM film_genres WHERE film_id = ?";
        String sqlQuery3 = "DELETE FROM film_directors WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery2, film.getId());
        jdbcTemplate.update(sqlQuery3, film.getId());

        if (film.getGenres() != null) {
            film.setGenres(addGenresToTable(film));
        }
        if (film.getDirectors() != null) {
            film.setDirectors(addDirectorToTable(film));
        }
        return film;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        String sqlQuery = "SELECT * FROM FILMS f JOIN mpa m ON f.MPA_ID = m.ID WHERE film_id=?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), id).stream().findAny();
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sqlQuery = "SELECT * FROM films f JOIN MPA M ON M.ID = f.MPA_ID " +
                "LEFT JOIN (SELECT film_id, COUNT(user_id) likes_quantity FROM likes " +
                "GROUP BY film_id) l ON f.film_id = l.FILM_ID ORDER BY likes_quantity DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), count);
    }

    @Override
    public List<Film> getSortedCommonFilms(Long userId, Long friendId) {
        String sqlQuery = "SELECT * FROM films f JOIN MPA M ON M.ID = f.MPA_ID " +
                "JOIN likes l ON l.film_id = f.film_id " +
                "JOIN likes l2 ON l2.film_id = f.film_id " +
                "WHERE l.USER_ID = ? AND l2.USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), userId, friendId);
    }

    @Override
    public List<Film> getSortedListOfFilmsByDirector(int directorId, String sortBy) {
        String sqlQuery = "";
        if (sortBy.equals("likes")) {
            sqlQuery = "SELECT * FROM films f " +
                    "JOIN MPA M ON M.ID = f.MPA_ID " +
                    "JOIN film_directors fd ON f.film_id = fd.film_id " +
                    "LEFT JOIN (SELECT film_id, COUNT(user_id) likes_quantity FROM likes " +
                    "GROUP BY film_id) l ON f.film_id = l.FILM_ID " +
                    "WHERE fd.director_id = ? " +
                    "ORDER BY likes_quantity DESC";
        } else if (sortBy.equals("year")) {
            sqlQuery = "SELECT * FROM films f " +
                    "JOIN MPA M ON M.ID = f.MPA_ID " +
                    "JOIN film_directors fd ON f.film_id = fd.film_id " +
                    "WHERE fd.director_id = ? " +
                    "ORDER BY YEAR(f.RELEASE_DATE)";
        }
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), directorId);
    }

    @Override
    public List<Film> searchFilms(String query, List<String> by) {
        String sqlQuery;
        String keyWord = '%' + query + '%';
        if (by.contains("title") && !by.contains("director")) {
            sqlQuery = "SELECT * FROM FILMS f " +
                    "LEFT JOIN MPA M ON M.ID = f.MPA_ID " +
                    "LEFT JOIN LIKES FL ON f.FILM_ID = FL.FILM_ID " +
                    "LEFT JOIN film_directors fd ON f.FILM_ID = fd.film_id " +
                    "LEFT JOIN directors d ON fd.DIRECTOR_ID = d.ID " +
                    "WHERE UPPER(f.film_name) LIKE UPPER(?) " +
                    "GROUP BY f.FILM_ID " +
                    "ORDER BY count(USER_ID) desc";
            return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), keyWord);
        } else if (by.contains("director") && !by.contains("title")) {
            sqlQuery = "SELECT * FROM FILMS f " +
                    "LEFT JOIN MPA M ON M.ID = f.MPA_ID " +
                    "LEFT JOIN LIKES FL ON f.FILM_ID = FL.FILM_ID " +
                    "LEFT JOIN film_directors fd ON f.FILM_ID = fd.film_id " +
                    "LEFT JOIN directors d ON fd.DIRECTOR_ID = d.ID " +
                    "WHERE UPPER(d.name) LIKE UPPER(?) " +
                    "GROUP BY f.FILM_ID " +
                    "ORDER BY count(USER_ID) desc";
            return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), keyWord);
        } else {
            sqlQuery = "SELECT * FROM FILMS f " +
                    "LEFT JOIN MPA M ON M.ID = f.MPA_ID " +
                    "LEFT JOIN LIKES FL ON f.FILM_ID = FL.FILM_ID " +
                    "LEFT JOIN film_directors fd ON f.FILM_ID = fd.film_id " +
                    "LEFT JOIN directors d ON fd.DIRECTOR_ID = d.ID " +
                    "WHERE UPPER(f.film_name) LIKE UPPER(?) OR UPPER(d.name) LIKE UPPER(?) " +
                    "GROUP BY f.FILM_ID " +
                    "ORDER BY count(USER_ID) desc";
            return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), keyWord, keyWord);
        }
    }

    private Genre mapRowToGenres(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(
                resultSet.getInt("id"),
                resultSet.getString("name"));
    }

    private Film makeFilm(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("film_id");
        String name = resultSet.getString("film_name");
        String description = resultSet.getString("film_description");
        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        Integer duration = resultSet.getInt("duration");
        Mpa mpa = new Mpa(resultSet.getInt("mpa_id"), resultSet.getString("name"));
        List<Genre> genres = makeGenres(id);
        Set<Genre> genresSet = new TreeSet<>(Comparator.comparing(Genre::getId));
        genresSet.addAll(genres);
        List<Director> directors = makeDirectors(id);
        Set<Director> directorsSet = new TreeSet<>(Comparator.comparing(Director::getId));
        directorsSet.addAll(directors);
        return new Film(id, name, description, releaseDate, duration, mpa, genresSet, directorsSet);
    }

    private List<Genre> makeGenres(Long id) {
        String sqlQuery = "SELECT * FROM genre " +
                "WHERE id IN (SELECT genre_id " +
                "FROM film_genres " +
                "WHERE film_id = ?) ORDER BY id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenres, id);
    }

    private Set<Genre> addGenresToTable(Film film) {
        String sqlQuery = "INSERT INTO film_genres (film_id, genre_id) " +
                "VALUES (?, ?)";
        TreeSet<Genre> sortedGenre = null;
        if (film.getGenres() != null) {
            sortedGenre = new TreeSet<>(Comparator.comparing(Genre::getId));
            sortedGenre.addAll(film.getGenres());
            for (Genre genre : sortedGenre) {
                jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
            }
        }
        return sortedGenre;
    }

    private Set<Director> addDirectorToTable(Film film) {
        String sqlQuery = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";
        if (film.getDirectors() != null) {
            film.getDirectors().forEach(director -> jdbcTemplate.update(sqlQuery, film.getId(), director.getId()));
        }
        return film.getDirectors();
    }

    protected List<Director> makeDirectors(Long id) {
        String sqlQuery = "SELECT * FROM directors d " +
                "JOIN film_directors fd ON d.id = fd.director_id " +
                "WHERE fd.film_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector, id);
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return new Director(
                resultSet.getInt("id"),
                resultSet.getString("name"));
    }
}
