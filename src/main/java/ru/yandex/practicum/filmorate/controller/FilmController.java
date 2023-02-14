package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

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
            throw new ValidationException("Невозможно обновить фильм");
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
            throw new ValidationException("Название фильма не может быть пустым");
        }
    }

    private void validateDescriptionLength(String description) {
        if (description == null || description.length() > MAX_CHARS_AMOUNT) {
            throw new ValidationException("Некорректное описание фильма");
        }
    }

    private void validateDate(LocalDate releaseDate) {
        if (releaseDate == null || releaseDate.isBefore(LocalDate.of(1895, 12, 28))
                || releaseDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Некорректная дата релиза фильма");
        }
    }

    private void validateDuration(Integer duration) {
        if (duration == null || duration < 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
