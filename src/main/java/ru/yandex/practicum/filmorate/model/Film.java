package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Film {
    private Integer id;
    @NotBlank
    @NotEmpty
    private String name;
    @Size(max = 50)
    private String description;
    @Past()
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
}
