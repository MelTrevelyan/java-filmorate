package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;

    private final EventService eventService;
    private long nextId = 1;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, EventService eventService) {
        this.filmStorage = filmStorage;
        this.eventService = eventService;
    }

    public Collection<Film> getFilms() {
        return Collections.unmodifiableCollection(filmStorage.getFilms().values());
    }

    public Film create(Film film) {
        FilmValidator.validateFilm(film);
        film.setId(getNextId());
        log.info("Добавлен новый фильм");
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        FilmValidator.validateFilm(film);
        if (filmStorage.findFilmById(film.getId()) == null) {
            log.warn("Невозможно обновить фильм");
            throw new FilmDoesNotExistException();
        }
        log.info("Фильм с id " + film.getId() + " был обновлён");
        return filmStorage.update(film);
    }

    public Film findFilmById(long id) {
        Film film = filmStorage.findFilmById(id);
        if (film == null) {
            throw new FilmDoesNotExistException();
        }
        return film;
    }

    public void addLike(long filmId, long userId) {
        filmStorage.addLike(filmId, userId);
        eventService.addEvent(userId, EventType.LIKE, EventOperation.ADD, filmId);
        log.info("Пользователь с id {} поставил фильму с id {} лайк", userId, filmId);
    }

    public void deleteLike(long filmId, long userId) {
        filmStorage.deleteLike(filmId, userId);
        log.info("Лайк пользователя с id {} фильму с id {} удалён", userId, filmId);
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getFilms().values().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public void deleteFilm(long filmId) {
        filmStorage.deleteFilm(filmId);
    }

    public List<Film> getFilmsByDirectorIdSortedByYearOrLikes(int directorId, String sortBy) {
        return filmStorage.getFilmsByDirectorIdSortedByYearOrLikes(directorId, sortBy);
    }

    private long getNextId() {
        return nextId++;
    }
}
