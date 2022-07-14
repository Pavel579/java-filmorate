package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage inMemoryFilmStorage;
    private final UserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(FilmStorage inMemoryFilmStorage, UserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
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
        if (inMemoryFilmStorage.getFilms().containsKey(film.getId())) {
            inMemoryFilmStorage.updateFilmInStorage(film);
        } else {
            throw new FilmNotFoundException("Такого фильма нет");
        }
    }

    public void setLikeToFilm(Long filmId, Long userId) {
        if (inMemoryFilmStorage.getFilms().containsKey(filmId)) {
            if (inMemoryUserStorage.getUsers().containsKey(userId)) {
                Film film = inMemoryFilmStorage.getFilms().get(filmId);
                film.addLike(userId);
                inMemoryFilmStorage.getFilms().put(filmId, film);
            } else {
                throw new UserNotFoundException("Такого пользователя нет");
            }
        } else {
            throw new FilmNotFoundException("Такого фильма нет");
        }
    }

    public void removeLike(Long filmId, Long userId) {
        if (inMemoryFilmStorage.getFilms().containsKey(filmId)) {
            if (inMemoryUserStorage.getUsers().containsKey(userId)) {
                Film film = inMemoryFilmStorage.getFilms().get(filmId);
                film.removeLike(userId);
                inMemoryFilmStorage.getFilms().put(filmId, film);
            } else {
                throw new UserNotFoundException("Такого пользователя нет");
            }
        } else {
            throw new FilmNotFoundException("Такого фильма нет");
        }
    }

    public List<Film> getCountLikes(Integer count) {
        return getAllFilms().stream()
                .sorted(Comparator.comparing(Film::getQuantityOfLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
