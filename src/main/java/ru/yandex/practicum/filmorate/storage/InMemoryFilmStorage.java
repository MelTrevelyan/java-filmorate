package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private Integer nextId = 1;

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        FilmValidator.validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм");
        return film;
    }

    @Override
    public Film update(Film film) {
        FilmValidator.validateFilm(film);
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
}
