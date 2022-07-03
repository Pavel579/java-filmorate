package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    FilmController filmController;
    Film film;

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController();
        film = new Film(1, "Terminator", "action", LocalDate.of(1984, 10, 26), 120);
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
