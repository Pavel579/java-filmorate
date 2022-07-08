package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private Long filmId = 0L;

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film saveFilm(@Valid @RequestBody Film film) {
        log.info("create film");
        validateFilm(film);
        log.debug("film passed validation");
        film.setId(++filmId);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("update film");
        validateFilm(film);
        log.debug("film passed validation");
        if (films.containsKey(film.getId())) {
            films.replace(film.getId(), film);
        } else {
            throw new ValidationException("Такого фильма нет");
        }
        return film;
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            throw new ValidationException("Дата релиза фильма не должна быть до 28.12.1895");
        }
    }
}
