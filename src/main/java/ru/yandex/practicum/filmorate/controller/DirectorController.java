package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.List;

@RestController
@RequestMapping
public class DirectorController {
    private final FilmService filmService;

    @Autowired
    public DirectorController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/directors")
    public List<Director> getDirectors() {
        return filmService.getDirectors();
    }

    @GetMapping("/directors/{id}")
    public Director getDirectorById(@PathVariable int id) {
        return filmService.getDirectorById(id);
    }

    @PostMapping("/directors")
    public Director createDirector(@Validated @RequestBody Director director) {
        return filmService.createDirector(director);
    }

    @PutMapping("/directors")
    public Director updateDirector(@Validated @RequestBody Director director) {
        return filmService.updateDirector(director);
    }

    @DeleteMapping("/directors/{id}")
    public void deleteDirector(@PathVariable int id) {
        filmService.deleteDirector(id);
    }
}
