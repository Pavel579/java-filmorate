package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Film {
    private Long id;
    @NotBlank
    @NonNull
    private String name;
    @Size(max = 200)
    @NonNull
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @Positive
    @NonNull
    private Integer duration;
    @NotNull
    @NonNull
    private Mpa mpa;
    @NonNull
    private Set<Genre> genres;
    @NonNull
    private Set<Director> directors;
}
