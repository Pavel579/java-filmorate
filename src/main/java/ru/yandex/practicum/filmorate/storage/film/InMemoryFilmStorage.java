package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private Long filmId = 0L;

    public Map<Long, Film> getFilms() {
        return new HashMap<>(films);
    }

    @Override
    public void addFilmToStorage(Film film) {
        film.setId(++filmId);
        films.put(film.getId(), film);
    }

    @Override
    public void deleteFilmFromStorage(Long filmId) {
        films.remove(filmId);
    }

    @Override
    public void updateFilmInStorage(Film film) {
        films.replace(film.getId(), film);
    }

    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }
}
