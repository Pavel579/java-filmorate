package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.LikesStorage;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmDbStorage;
    private final MpaStorage mpaDbStorage;
    private final GenreStorage genreDbStorage;
    private final LikesStorage likesDbStorage;
    private final DirectorStorage directorDbStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmDbStorage, MpaStorage mpaStorage,
                       GenreStorage genreDbStorage, LikesStorage likesDbStorage,
                       DirectorStorage directorDbStorage, UserService userService) {
        this.filmDbStorage = filmDbStorage;
        this.mpaDbStorage = mpaStorage;
        this.genreDbStorage = genreDbStorage;
        this.likesDbStorage = likesDbStorage;
        this.directorDbStorage = directorDbStorage;
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
        likesDbStorage.setLikeToFilm(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        userService.getUserById(userId);
        likesDbStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmDbStorage.getPopularFilms(count);
    }

    public Mpa getMpaById(Integer id) {
        return mpaDbStorage.getMpaById(id).orElseThrow(() -> new MpaNotFoundException("MPA не найден"));
    }

    public List<Mpa> getAllMpa() {
        return mpaDbStorage.getAllMpa();
    }

    public List<Genre> getAllGenres() {
        return genreDbStorage.getAllGenres();
    }

    public Genre getGenreById(Integer id) {
        return genreDbStorage.getGenreById(id).orElseThrow(() -> new GenreNotFoundException("Genre не найден"));
    }

    public List<Director> getDirectors() {
        return directorDbStorage.getDirectors();
    }

    public Director getDirectorById(int id) {
        return directorDbStorage.getDirectorById(id).orElseThrow(() -> new DirectorNotFoundException("Режиссер не найден"));
    }

    public Director createDirector(Director director) {
        return directorDbStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        getDirectorById(director.getId());
        return directorDbStorage.updateDirector(director);
    }

    public void deleteDirector(int id) {
        getDirectorById(id);
        directorDbStorage.deleteDirector(id);
    }

    public List<Film> getSortedListOfFilmsByDirector(int directorId, String sortBy) {
        getDirectorById(directorId);
        return filmDbStorage.getSortedListOfFilmsByDirector(directorId, sortBy);
    }
}