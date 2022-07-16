package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage inMemoryFilmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage inMemoryFilmStorage, UserService userService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.userService = userService;
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(inMemoryFilmStorage.getFilms().values());
    }

    public Film getFilmById(Long id) {
        return inMemoryFilmStorage.getFilmById(id).orElseThrow(() -> new FilmNotFoundException("Фильм не найден"));
    }

    public void addFilmToStorage(Film film) {
        inMemoryFilmStorage.addFilmToStorage(film);
    }

    public void updateFilmInStorage(Film film) {
        getFilmById(film.getId());
        inMemoryFilmStorage.updateFilmInStorage(film);
    }

    public void setLikeToFilm(Long filmId, Long userId) {
        userService.getUserById(userId);
        Film film = getFilmById(filmId);
        film.addLike(userId);
        inMemoryFilmStorage.getFilms().put(filmId, film);
    }

    public void removeLike(Long filmId, Long userId) {
        userService.getUserById(userId);
        Film film = getFilmById(filmId);
        film.removeLike(userId);
        inMemoryFilmStorage.getFilms().put(filmId, film);
    }

    public List<Film> getCountLikes(Integer count) {
        return getAllFilms().stream()
                .sorted(Comparator.comparing(Film::getQuantityOfLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
