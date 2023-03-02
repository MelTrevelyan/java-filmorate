package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private long nextId = 1;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getFilms() {
        return  filmStorage.getFilms();
    }

    public Film create(Film film) {
        FilmValidator.validateFilm(film);
        film.setId(getNextId());
        log.info("Добавлен новый фильм");
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        FilmValidator.validateFilm(film);
        if (!filmStorage.getFilms().contains(film.getId())) {
            log.warn("Невозможно обновить фильм");
            throw new ValidationException();
        }
        log.info("Фильм с id " + film.getId() + " был обновлён");
        return filmStorage.update(film);
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.findFilmById(filmId).getLikes().add(userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        filmStorage.findFilmById(filmId).getLikes().remove(userId);
    }

    public List<Film> getMostPopularFilms(int count) {
       return filmStorage.getFilms().stream()
               .sorted(Comparator.comparingInt(f -> f.getLikes().size()))
               .limit(count)
               .collect(Collectors.toList());
    }

    private long getNextId() {
        return nextId++;
    }
}
