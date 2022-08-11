package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    //Метод возвращает список всех фильмов из базы данных
    List<Film> getFilms();

    //Метод добавляет фильм в базу данных
    void addFilmToStorage(Film film);

    //Метод удаляет фильм из базы данных с id = filmId
    void deleteFilmFromStorage(Long filmId);

    //Метод обновляет фильм в базе данных
    Film updateFilmInStorage(Film film);

    //Метод возвращает фильм из базы данных с id = id
    Optional<Film> getFilmById(Long id);

    //Метод возвращает список размерностью count самых популярных фильмов по кол-ву лайков
    List<Film> getPopularFilms(Integer count);
}