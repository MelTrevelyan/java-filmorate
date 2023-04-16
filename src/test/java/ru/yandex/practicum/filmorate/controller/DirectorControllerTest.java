package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorControllerTest {
    private final DirectorService directorService;

    @Test
    public void shouldDeleteDirectorById() {
        Director director1 = new Director(1, "Александр Невский");
        directorService.addDirector(director1);
        Director director2 = new Director(2, "Друг Шварценеггера");
        directorService.addDirector(director2);

        directorService.deleteDirectorById(2);
        assertEquals(directorService.getAllDirectors().size(), 3);
    }

    @Test
    public void shouldAddDirector() {
        Director director3 = new Director(3, "Александр Невский");
        directorService.addDirector(director3);

        assertEquals(directorService.getDirectorById(3).getName(), "Александр Невский");
    }

    @Test
    public void shouldUpdateDirector() {
        Director director = new Director(1, "Обновили, уот так уот");
        directorService.updateDirector(director);

        assertEquals(directorService.getDirectorById(1).getName(), "Обновили, уот так уот");
    }

    @Test
    public void shouldGetAllDirectors() {
        assertEquals(directorService.getAllDirectors().size(), 4);
    }

    @Test
    public void shouldGetDirectorById() {
        assertEquals(directorService.getDirectorById(3).getName(), "Александр Невский");
    }
}
