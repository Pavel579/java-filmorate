package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

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

    //Метод добавляет к фильму с id = filmId like от пользователя с id = userId
    void setLikeToFilm(Long filmId, Long userId);

    //Метод удаляет like у фильма с id = filmId от пользователя с id = userId
    void removeLike(Long filmId, Long userId);

    //Метод возвращает список размерностью count самых популярных фильмов по кол-ву лайков
    List<Film> getPopularFilms(Integer count);

    //Метод возвращает список всех рейтингов (MPA - Motion Picture Association)
    List<Mpa> getAllMpa();

    //Метод возвращает MPA по его id
    Optional<Mpa> getMpaById(Integer id);

    //Метод возвращает список всех возможных жанров
    List<Genre> getAllGenres();

    //Метод возвращает жанр по его id
    Optional<Genre> getGenreById(Integer id);
}
