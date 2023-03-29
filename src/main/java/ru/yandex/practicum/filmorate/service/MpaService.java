package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> getAllMpa() {
        return List.copyOf(mpaStorage.getAllMpa().values());
    }

    public Mpa getMpaById(int id) {
        Mpa mpa = mpaStorage.findMpaById(id);
        if (mpa == null) {
            log.warn("Рейтинга с id {} не найдено", id);
            throw new MpaDoesNotExistException();
        }
        return mpa;
    }
}
