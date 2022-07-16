package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;
import java.util.Optional;

public interface FilmStorage {

    Map<Long, Film> getFilms();

    void addFilmToStorage(Film film);

    void deleteFilmFromStorage(Long filmId);

    void updateFilmInStorage(Film film);

    Optional<Film> getFilmById(Long id);
}
