package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @PostMapping
    public Film saveFilm(@Valid @RequestBody Film film) {
        log.info("create film");
        validateFilm(film);
        log.debug("film passed validation");
        filmService.addFilmToStorage(film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("update film");
        validateFilm(film);
        log.debug("film passed validation");
        filmService.updateFilmInStorage(film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLikeToFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.setLikeToFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getCountLikes(@RequestParam(required = false) Integer count) {
        return filmService.getCountLikes(count);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            throw new ValidationException("Дата релиза фильма не должна быть до 28.12.1895");
        }
    }
}
