package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    FilmController filmController;
    Film film;

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new UserService(new InMemoryUserStorage())));
        film = new Film(1L, "Terminator", "action", LocalDate.of(1984, 10, 26), 5, new HashSet<>());
    }

    @Test
    void createFilm() {
        filmController.saveFilm(film);
        assertEquals(film.getName(), filmController.getAllFilms().get(0).getName());
    }

    @Test
    void createFilmWithWrongData() {
        film.setReleaseDate(LocalDate.of(1777, 5, 2));
        Throwable exception = assertThrows(RuntimeException.class, () -> filmController.saveFilm(film));
        assertEquals("Дата релиза фильма не должна быть до 28.12.1895", exception.getMessage());
    }
}
