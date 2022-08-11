package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Setter
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    @Email
    @NonNull
    private String email;
    @NotBlank
    @NonNull
    private String login;
    @NonNull
    private String name;
    @Past
    @NonNull
    private LocalDate birthday;
}
