package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {

    Map<Long, Film> getFilms();

    Film create(Film film);

    Film update(Film film);

    Film findFilmById(long id);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    void deleteFilm(long filmId);

    Optional<List<Film>> getFilmsByDirectorIdSortedByYearOrLikes(int id, String sortBy);
}
