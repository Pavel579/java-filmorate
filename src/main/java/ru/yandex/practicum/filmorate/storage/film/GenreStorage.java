package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    //Метод возвращает список всех возможных жанров
    List<Genre> getAllGenres();

    //Метод возвращает жанр по его id
    Optional<Genre> getGenreById(Integer id);
}