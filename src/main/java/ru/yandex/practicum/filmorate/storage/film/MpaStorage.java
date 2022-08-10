package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {
    //Метод возвращает список всех рейтингов (MPA - Motion Picture Association)
    List<Mpa> getAllMpa();

    //Метод возвращает MPA по его id
    Optional<Mpa> getMpaById(Integer id);
}