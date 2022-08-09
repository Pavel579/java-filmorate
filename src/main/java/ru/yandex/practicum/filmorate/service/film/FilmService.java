package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmDbStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmDbStorage, UserService userService) {
        this.filmDbStorage = filmDbStorage;
        this.userService = userService;
    }

    public List<Film> getAllFilms() {
        return filmDbStorage.getFilms();
    }

    public Film getFilmById(Long id) {
        return filmDbStorage.getFilmById(id).orElseThrow(() -> new FilmNotFoundException("Фильм не найден"));
    }

    public void addFilmToStorage(Film film) {
        filmDbStorage.addFilmToStorage(film);
    }

    public Film updateFilmInStorage(Film film) {
        getFilmById(film.getId());
        return filmDbStorage.updateFilmInStorage(film);
    }

    public void setLikeToFilm(Long filmId, Long userId) {
        userService.getUserById(userId);
        filmDbStorage.setLikeToFilm(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        userService.getUserById(userId);
        filmDbStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmDbStorage.getPopularFilms(count);
    }

    public Mpa getMpaById(Integer id) {
        return filmDbStorage.getMpaById(id).orElseThrow(() -> new MpaNotFoundException("MPA не найден"));
    }

    public List<Mpa> getAllMpa() {
        return filmDbStorage.getAllMpa();
    }

    public List<Genre> getAllGenres() {
        return filmDbStorage.getAllGenres();
    }

    public Genre getGenreById(Integer id) {
        return filmDbStorage.getGenreById(id).orElseThrow(() -> new GenreNotFoundException("Genre не найден"));
    }
}
