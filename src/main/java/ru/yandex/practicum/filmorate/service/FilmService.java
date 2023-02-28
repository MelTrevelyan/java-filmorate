package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getFilms() {
        return  filmStorage.getFilms();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.findFilmById(filmId).getLikes().add(userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        filmStorage.findFilmById(filmId).getLikes().remove(userId);
    }

    public List<Film> getMostPopularFilms() {
       return filmStorage.getFilms().stream()
               .sorted(Comparator.comparingInt(f -> f.getLikes().size()))
               .limit(10)
               .collect(Collectors.toList());
    }
}
