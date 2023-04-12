package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    Map<Long, Film> getFilms();

    Film create(Film film);

    Film update(Film film);

    Film findFilmById(long id);

    void addLike(long filmId, long userId);

    List<Film> getFilmsByDirectorOrTitle(String query, String by);

    void createDirector(String name);

    void deleteLike(long filmId, long userId);

    void deleteFilm(long filmId);
}
