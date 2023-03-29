package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@Slf4j
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getAllGenres() {
        return List.copyOf(genreStorage.getAllGenres().values());
    }

    public Genre getGenreById(int id) {
        Genre genre = genreStorage.findGenreById(id);
        if (genre == null) {
            log.warn("Жанр с id = {} не найден", id);
            throw new GenreDoesNotExistException();
        }
        return genre;
    }
}
