package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.impl.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.film.impl.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.film.impl.MpaDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final LikesDbStorage likesDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    Film film1;
    Film film2;
    Film film3;
    Mpa mpa = new Mpa(1, "G");
    Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));

    @BeforeEach
    public void beforeEach() {
        genres.add(new Genre(1, "Комедия"));
        genres.add(new Genre(3, "Мультфильм"));
        film1 = new Film("film1", "description1",
                LocalDate.of(2005, 2, 15), 120, mpa, genres);
        film2 = new Film("film2", "description2",
                LocalDate.of(2008, 5, 7), 60, mpa, genres);
        film3 = new Film("film3", "description3",
                LocalDate.of(2007, 8, 20), 180, mpa, genres);
    }

    @Test
    public void testAddFilmToStorageAndGetFilmById() {
        filmDbStorage.addFilmToStorage(film1);
        Optional<Film> filmOptional = filmDbStorage.getFilmById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testGetFilms() {
        filmDbStorage.addFilmToStorage(film1);
        filmDbStorage.addFilmToStorage(film2);
        List<Film> filmsList = new ArrayList<>(filmDbStorage.getFilms());
        Optional<Film> filmOptional = Optional.of(filmsList.get(1));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description", "description2")
                );
    }

    @Test
    public void testDeleteFilmFromStorage() {
        filmDbStorage.addFilmToStorage(film1);
        filmDbStorage.addFilmToStorage(film2);
        filmDbStorage.deleteFilmFromStorage(film2.getId());
        List<Film> filmsList = new ArrayList<>(filmDbStorage.getFilms());
        assertEquals(filmsList.size(), 1);
    }

    @Test
    public void testUpdateFilmInStorage() {
        filmDbStorage.addFilmToStorage(film1);
        filmDbStorage.addFilmToStorage(film2);
        Film film3 = new Film(2L, "Updatedfilm2", "Updateddescription2",
                LocalDate.of(2009, 5, 7), 60, mpa, genres);
        filmDbStorage.updateFilmInStorage(film3);
        List<Film> filmsList = new ArrayList<>(filmDbStorage.getFilms());
        Optional<Film> userOptional = Optional.of(filmsList.get(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description", "Updateddescription2")
                );
    }

    @Test
    public void testSetLikeToFilm() {
        filmDbStorage.addFilmToStorage(film1);
        filmDbStorage.addFilmToStorage(film2);
        filmDbStorage.addFilmToStorage(film3);
        likesDbStorage.setLikeToFilm(2L, 1L);
        likesDbStorage.setLikeToFilm(2L, 2L);
        likesDbStorage.setLikeToFilm(3L, 4L);
        List<Film> filmsList = new ArrayList<>(filmDbStorage.getPopularFilms(2));
        Optional<Film> film1Optional = Optional.of(filmsList.get(0));
        Optional<Film> film2Optional = Optional.of(filmsList.get(1));
        assertThat(film1Optional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description", "description2")
                );
        assertThat(film2Optional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description", "description3")
                );
        assertEquals(filmsList.size(), 2);
    }

    @Test
    public void testRemoveLike() {
        filmDbStorage.addFilmToStorage(film1);
        filmDbStorage.addFilmToStorage(film2);
        likesDbStorage.setLikeToFilm(1L, 1L);
        likesDbStorage.setLikeToFilm(2L, 4L);
        likesDbStorage.setLikeToFilm(2L, 5L);
        List<Film> filmsList = new ArrayList<>(filmDbStorage.getPopularFilms(2));
        Optional<Film> film1Optional = Optional.of(filmsList.get(0));
        assertThat(film1Optional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description", "description2")
                );
        likesDbStorage.removeLike(2L, 4L);
        likesDbStorage.removeLike(2L, 5L);
        filmsList.clear();
        filmsList.addAll(filmDbStorage.getPopularFilms(2));
        film1Optional = Optional.of(filmsList.get(0));
        assertThat(film1Optional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description", "description1")
                );
    }

    @Test
    public void testGetAllMpa() {
        List<Mpa> mpaList = new ArrayList<>(mpaDbStorage.getAllMpa());
        Optional<Mpa> mpaOptional = Optional.of(mpaList.get(3));
        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("name", "R")
                );
        assertEquals(mpaList.size(), 5);
    }

    @Test
    public void testGetMpaById() {
        Optional<Mpa> mpaOptional = mpaDbStorage.getMpaById(2);
        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("name", "PG")
                );
    }

    @Test
    public void testGetAllGenres() {
        List<Genre> genreList = new ArrayList<>(genreDbStorage.getAllGenres());
        Optional<Genre> genreOptional = Optional.of(genreList.get(3));
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("name", "Триллер")
                );
        assertEquals(genreList.size(), 6);
    }

    @Test
    public void testGetGenreById() {
        Optional<Genre> genreOptional = genreDbStorage.getGenreById(2);
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("name", "Драма")
                );
    }
}