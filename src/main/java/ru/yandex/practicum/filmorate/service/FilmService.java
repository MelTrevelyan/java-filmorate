package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private long nextId = 1;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService) {
        this.userService = userService;
        this.filmStorage = filmStorage;
    }

    public List<Film> getFilms() {
        return List.copyOf(filmStorage.getFilms().values());
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

    public void addLike(Long filmId, Long userId) {
        if (userService.findUserById(userId) != null) {
            findFilmById(filmId).getLikes().add(userId);
            log.info("Пользователь с id {} поставил фильму с id {} лайк", userId, filmId);
        }
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = findFilmById(filmId);
        if (userService.findUserById(userId) != null) {
            film.getLikes().remove(userId);
            log.info("Лайк пользователя с id {} фильму с id {} удалён", userId, filmId);
        }
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getFilms().values().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    private long getNextId() {
        return nextId++;
    }
}
