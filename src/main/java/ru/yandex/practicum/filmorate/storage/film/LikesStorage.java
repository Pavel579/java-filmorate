package ru.yandex.practicum.filmorate.storage.film;

public interface LikesStorage {
    //Метод добавляет к фильму с id = filmId like от пользователя с id = userId
    void setLikeToFilm(Long filmId, Long userId);

    //Метод удаляет like у фильма с id = filmId от пользователя с id = userId
    void removeLike(Long filmId, Long userId);
}