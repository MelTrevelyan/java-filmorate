package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;
    private long nextId = 1;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, EventStorage eventStorage) {
        this.filmStorage = filmStorage;
        this.eventStorage = eventStorage;
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
        Event event = new Event(userId, EventType.LIKE, EventOperation.ADD, filmId);
        eventStorage.addEvent(event);
        log.info("Пользователь с id {} поставил фильму с id {} лайк", userId, filmId);
    }

    public void deleteLike(long filmId, long userId) {
        filmStorage.deleteLike(filmId, userId);
        Event event = new Event(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
        eventStorage.addEvent(event);
        log.info("Лайк пользователя с id {} фильму с id {} удалён", userId, filmId);
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getFilms().values().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> getFilmsByDirectorOrTitle(String query, String by) {

        AtomicReference<String> director = new AtomicReference<>("");
        AtomicReference<String> title = new AtomicReference<>("");
        Arrays.stream(by.split(",")).forEach(q -> {
            if (q.trim().equals("director")) director.set("director");
            if (q.trim().equals("title")) title.set("title");
        });
        StringBuilder queryBuilder = new StringBuilder("%");
        queryBuilder.append(query.toLowerCase()).append("%");
        return filmStorage.getFilmsByDirectorOrTitle(queryBuilder.toString(), director.get(), title.get());
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
