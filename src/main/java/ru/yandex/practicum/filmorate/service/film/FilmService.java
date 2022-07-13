package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(inMemoryFilmStorage.getFilms().values());
    }

    public Film getFilmById(Long id) {
        if (inMemoryFilmStorage.getFilms().containsKey(id)) {
            return inMemoryFilmStorage.getFilms().get(id);
        } else {
            throw new FilmNotFoundException("Такого фильма нет");
        }
    }

    public void addFilmToStorage(Film film) {
        inMemoryFilmStorage.addFilmToStorage(film);
    }

    public void updateFilmInStorage(Film film) {
        if (inMemoryFilmStorage.getFilms().containsKey(film.getId())) {
            inMemoryFilmStorage.updateFilmInStorage(film);
        } else {
            throw new FilmNotFoundException("Такого фильма нет");
        }
    }

    public void setLikeToFilm(Long filmId, Long userId) {
        if (inMemoryFilmStorage.getFilms().containsKey(filmId)) {
            if (inMemoryUserStorage.getUsers().containsKey(userId)) {
                Film film = inMemoryFilmStorage.getFilms().get(filmId);
                film.addLike(userId);
                inMemoryFilmStorage.getFilms().put(filmId, film);
            } else {
                throw new UserNotFoundException("Такого пользователя нет");
            }
        } else {
            throw new FilmNotFoundException("Такого фильма нет");
        }
    }

    public void removeLike(Long filmId, Long userId) {
        if (inMemoryFilmStorage.getFilms().containsKey(filmId)) {
            if (inMemoryUserStorage.getUsers().containsKey(userId)) {
                Film film = inMemoryFilmStorage.getFilms().get(filmId);
                film.removeLike(userId);
                inMemoryFilmStorage.getFilms().put(filmId, film);
            } else {
                throw new UserNotFoundException("Такого пользователя нет");
            }
        } else {
            throw new FilmNotFoundException("Такого фильма нет");
        }
    }

    public List<Film> getCountLikes(Integer count) {
        List<Film> filmsSortedByLikes = new ArrayList<>(inMemoryFilmStorage.getFilms().values());
        List<Film> resultList = new ArrayList<>();
        filmsSortedByLikes.sort(Comparator.comparing(Film::getQuantityOfLikes).reversed());
        if (count != null) {
            if (count >= filmsSortedByLikes.size()) {
                resultList.addAll(filmsSortedByLikes);
            } else {
                for (int i = 0; i < count; i++) {
                    resultList.add(filmsSortedByLikes.get(i));
                }
            }
        } else {
            if (filmsSortedByLikes.size() > 10) {
                for (int i = 0; i < 10; i++) {
                    resultList.add(filmsSortedByLikes.get(i));
                }
            } else {
                resultList.addAll(filmsSortedByLikes);
            }
        }
        return resultList;
    }
}
