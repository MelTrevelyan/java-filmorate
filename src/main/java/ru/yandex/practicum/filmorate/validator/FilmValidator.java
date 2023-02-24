package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class FilmValidator {

    public static final int MAX_CHARS_AMOUNT = 200;

    public static void validateFilm(Film film) {
        validateName(film.getName());
        validateDescriptionLength(film.getDescription());
        validateDate(film.getReleaseDate());
        validateDuration(film.getDuration());
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank() || name.isEmpty()) {
            log.warn("Ошибка валидации фильма. Название не может быть пустым");
            throw new ValidationException();
        }
    }

    private static void validateDescriptionLength(String description) {
        if (description == null || description.length() > MAX_CHARS_AMOUNT) {
            log.warn("Ошибка валидации фильма. Некорректное описание фильма");
            throw new ValidationException();
        }
    }

    private static void validateDate(LocalDate releaseDate) {
        if (releaseDate == null || releaseDate.isBefore(LocalDate.of(1895, 12, 28))
                || releaseDate.isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации фильма. Некорректная дата релиза фильма");
            throw new ValidationException();
        }
    }

    private static void validateDuration(Integer duration) {
        if (duration == null || duration < 0) {
            log.warn("Ошибка валидации фильма. Продолжительность фильма должна быть положительной");
            throw new ValidationException();
        }
    }
}
