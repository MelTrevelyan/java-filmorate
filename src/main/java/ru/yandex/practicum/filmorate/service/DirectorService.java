package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
public class DirectorService {
    private final DirectorStorage directorDbStorage;

    @Autowired
    public DirectorService(DirectorStorage directorDBStorage) {
        this.directorDbStorage = directorDBStorage;
    }

    public List<Director> getAllDirectors() {
        return directorDbStorage.getAllDirectors();
    }

    public Director getDirectorById(int id) {
        return directorDbStorage.getDirectorById(id);
    }

    public Director addDirector(Director director) {
        return directorDbStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorDbStorage.updateDirector(director);
    }

    public void deleteDirectorById(int id) {
        directorDbStorage.deleteDirectorById(id);
    }
}
