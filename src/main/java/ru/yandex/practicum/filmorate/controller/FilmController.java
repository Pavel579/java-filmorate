package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping
@Slf4j
@Validated
public class FilmController {
    private final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @PostMapping("/films")
    public Film saveFilm(@Valid @RequestBody Film film) {
        log.info("create film");
        validateFilm(film);
        log.debug("film passed validation");
        return filmService.addFilmToStorage(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("update film");
        validateFilm(film);
        log.debug("film passed validation");
        return filmService.updateFilmInStorage(film);
    }

    @DeleteMapping("/films/{filmId}")
    public void removeFilmById(@PathVariable Long filmId){
        filmService.removeFilmById(filmId);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void setLikeToFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.setLikeToFilm(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") @Positive Integer count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/films/director/{directorId}")
    public List<Film> getSortedListOfFilmsByDirector(@PathVariable int directorId, @RequestParam String sortBy){
        return filmService.getSortedListOfFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("")
    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            throw new ValidationException("Дата релиза фильма не должна быть до 28.12.1895");
        }
    }
}
