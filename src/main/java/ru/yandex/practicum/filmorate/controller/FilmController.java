package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    public static final int MAX_CHARS_AMOUNT = 200;
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer nextId = 1;

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм");
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        validateFilm(film);
        if (!films.containsKey(film.getId())) {
            log.warn("Невозможно обновить фильм");
            throw new ValidationException();
        }
        films.put(film.getId(), film);
        log.info("Фильм с id " + film.getId() + " был обновлён");
        return film;
    }

    private int getNextId() {
        return nextId++;
    }

    private void validateFilm(Film film) {
        validateName(film.getName());
        validateDescriptionLength(film.getDescription());
        validateDate(film.getReleaseDate());
        validateDuration(film.getDuration());
    }

    private void validateName(String name) {
        if (name == null || name.isBlank() || name.isEmpty()) {
            log.warn("Ошибка валидации фильма. Название не может быть пустым");
            throw new ValidationException();
        }
    }

    private void validateDescriptionLength(String description) {
        if (description == null || description.length() > MAX_CHARS_AMOUNT) {
            log.warn("Ошибка валидации фильма. Некорректное описание фильма");
            throw new ValidationException();
        }
    }

    private void validateDate(LocalDate releaseDate) {
        if (releaseDate == null || releaseDate.isBefore(LocalDate.of(1895, 12, 28))
                || releaseDate.isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации фильма. Некорректная дата релиза фильма");
            throw new ValidationException();
        }
    }

    private void validateDuration(Integer duration) {
        if (duration == null || duration < 0) {
            log.warn("Ошибка валидации фильма. Продолжительность фильма должна быть положительной");
            throw new ValidationException();
        }
    }
}
