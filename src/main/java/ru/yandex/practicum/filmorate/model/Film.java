package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private Long id;
    @NotBlank
    @NotEmpty
    private String name;
    @Size(max = 50)
    private String description;
    @Past()
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    private Set<Long> likes = new HashSet<>();

    public void addLike(Long userId) {
        likes.add(userId);
    }

    public void removeLike(Long userId) {
        likes.remove(userId);
    }

    public Integer getQuantityOfLikes() {
        return likes.size();
    }
}
