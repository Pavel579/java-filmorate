package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    void addFilmToStorage(Film film);

    void deleteFilmFromStorage(Long filmId);

    void updateFilmInStorage(Film film);
}
