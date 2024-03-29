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

    List<Film> getFilmsByDirectorOrTitle(String query, String director, String title);


    void deleteLike(long filmId, long userId);

    void deleteFilm(long filmId);

    List<Film> getRecommendations(long userId);

    List<Film> getFilmsByDirectorIdSortedByYearOrLikes(int id, String sortBy);
}
