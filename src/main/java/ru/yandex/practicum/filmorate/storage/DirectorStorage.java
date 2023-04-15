package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    List<Director> getAllDirectors();

    Director getDirectorById(int id);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirectorById(int id);

    List<Director> getDirectorsByFilmId(long filmId);
}
