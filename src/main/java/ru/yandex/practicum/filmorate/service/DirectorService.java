package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
public class DirectorService {
    private final DirectorStorage directorDBStorage;

    @Autowired
    public DirectorService(DirectorStorage directorDBStorage) {
        this.directorDBStorage = directorDBStorage;
    }

    public List<Director> getAllDirectors() {
        return directorDBStorage.getAllDirectors();
    }

    public Director getDirectorById(int id){
        return directorDBStorage.getDirectorById(id);
    }

    public Director addDirector(Director director) {
        return directorDBStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorDBStorage.updateDirector(director);
    }

    public void deleteDirectorById(int id) {
        directorDBStorage.deleteDirectorById(id);
    }
}
