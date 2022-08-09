package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

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
        String sqlQuery = "SELECT * FROM films";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));
    }

    private Film makeFilm(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("film_id");
        String name = resultSet.getString("film_name");
        String description = resultSet.getString("film_description");
        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        Integer duration = resultSet.getInt("duration");
        Mpa mpa = makeMpa(id);
        List<Genre> genres = makeGenres(id);
        Set<Genre> genresSet = new TreeSet<>(Comparator.comparing(Genre::getId));
        genresSet.addAll(genres);
        return new Film(id, name, description, releaseDate, duration, mpa, genresSet);
    }

    private Mpa makeMpa(Long filmId) {
        String sqlQuery = "SELECT * FROM mpa " +
                "WHERE id = (SELECT mpa_id " +
                "FROM films " +
                "WHERE film_id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, filmId);
    }

    private List<Genre> makeGenres(Long id) {
        String sqlQuery = "SELECT * FROM genre " +
                "WHERE id IN (SELECT genre_id " +
                "FROM film_genres " +
                "WHERE film_id = ?) ORDER BY id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenres, id);
    }

    @Override
    public void addFilmToStorage(Film film) {
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

        Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
        String sqlQuery2 = "INSERT INTO film_genres (film_id, genre_id) " +
                "VALUES (?, ?)";
        if (film.getGenres() != null) {
            genres.addAll(film.getGenres());
            for (Genre genre : genres) {
                jdbcTemplate.update(sqlQuery2, film.getId(), genre.getId());
            }
        }
    }

    @Override
    public void deleteFilmFromStorage(Long filmId) {
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public Film updateFilmInStorage(Film film) {
        String sqlQuery = "UPDATE films SET " +
                "film_name = ?, film_description = ?, release_date = ?, " +
                "duration = ?, mpa_id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        String sqlQuery2 = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery2, film.getId());
        String sqlQuery3 = "INSERT INTO film_genres (film_id, genre_id) " +
                "VALUES (?, ?)";

        if (film.getGenres() != null) {
            TreeSet<Genre> sortedGenre = new TreeSet<>(Comparator.comparing(Genre::getId));
            sortedGenre.addAll(film.getGenres());
            for (Genre genre : sortedGenre) {
                jdbcTemplate.update(sqlQuery3, film.getId(), genre.getId());
            }
            film.setGenres(sortedGenre);
        }
        return film;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        String sqlQuery = "SELECT * FROM films " +
                "WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), id).stream().findAny();
    }

    @Override
    public void setLikeToFilm(Long filmId, Long userId) {
        String sqlQuery = "INSERT INTO likes (film_id, user_id) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM likes " +
                "WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sqlQuery = "SELECT * FROM films f LEFT JOIN (SELECT film_id, COUNT(user_id) likes_quantity FROM likes " +
                "GROUP BY film_id) l ON f.film_id = l.FILM_ID ORDER BY likes_quantity DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), count);
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT * FROM mpa ORDER BY id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public Optional<Mpa> getMpaById(Integer id) {
        String sqlQuery = "SELECT * FROM mpa WHERE id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa, id).stream().findAny();
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM genre ORDER BY id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenres);
    }

    @Override
    public Optional<Genre> getGenreById(Integer id) {
        String sqlQuery = "SELECT * FROM genre WHERE id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenres, id).stream().findAny();
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(
                resultSet.getInt("id"),
                resultSet.getString("name"));
    }

    private Genre mapRowToGenres(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(
                resultSet.getInt("id"),
                resultSet.getString("name"));
    }
}
