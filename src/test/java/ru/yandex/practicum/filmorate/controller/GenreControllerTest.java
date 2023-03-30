package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreControllerTest {

    private final GenreService genreService;

    @Test
    public void getAllGenres() {
        List<Genre> genres = genreService.getAllGenres();

        assertEquals(6, genres.size());
    }

    @Test
    public void findGenreById() {
        Genre genre = genreService.getGenreById(1);

        assertEquals("Комедия", genre.getName());
    }
}
