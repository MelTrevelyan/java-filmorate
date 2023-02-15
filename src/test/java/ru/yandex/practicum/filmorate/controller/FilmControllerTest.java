package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {

    private FilmController controller;

    @BeforeEach
    public void beforeEach() {
        controller = new FilmController();
    }

    @Test
    public void shouldPassValidation() {
        controller.create(Film.builder()
                .name("Аватар")
                .description("Путь воды")
                .duration(192)
                .releaseDate(LocalDate.of(2022, 12, 6))
                .build());

        assertEquals(1, controller.getFilms().size());
    }

    @Test
    public void shouldNotPassNameValidation() {
        Film film = Film.builder()
                .name("")
                .description("Уже не путь воды")
                .duration(192)
                .releaseDate(LocalDate.of(2022, 12, 6))
                .build();

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    public void shouldNotPassDescriptionValidation() {
        Film film = Film.builder()
                .name("Аватар")
                .description("«Аватар: Путь воды» (англ. Avatar: The Way of Water) — американский " +
                        "научно-фантастический фильм режиссёра и сценариста Джеймса Кэмерона. Является сиквелом" +
                        " фильма «Аватар» 2009 года. Изначально премьера фильма была запланирована на 17 декабря 2021" +
                        " года, но в связи с пандемией коронавируса 2020 года была перенесена на 16 декабря 2022 года.")
                .duration(192)
                .releaseDate(LocalDate.of(2022, 12, 6))
                .build();

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    public void shouldNotPassReleaseDateValidation() {
        Film film1 = Film.builder()
                .name("Аватар")
                .description("Путь воды")
                .duration(192)
                .releaseDate(LocalDate.of(1722, 12, 6))
                .build();
        Film film2 = Film.builder()
                .name("Аватар")
                .description("Ещё один путь воды")
                .duration(192)
                .releaseDate(LocalDate.of(2024, 12, 6))
                .build();

        assertThrows(ValidationException.class, () -> controller.create(film1));
        assertThrows(ValidationException.class, () -> controller.create(film2));
    }

    @Test
    public void shouldNotPassDurationValidation() {
        Film film = Film.builder()
                .name("Аватар")
                .description("Путь воды")
                .duration(-192)
                .releaseDate(LocalDate.of(2022, 12, 6))
                .build();

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    public void emptyFilmShouldNotPassValidation() {
        Film film = Film.builder().build();
        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    public void shouldUpdateFilm() {
        controller.create(Film.builder()
                .name("Аватар")
                .description("Путь воды")
                .duration(192)
                .releaseDate(LocalDate.of(2022, 12, 6))
                .build());

        controller.update(Film.builder()
                .id(1)
                .name("Зелёная книга")
                .description("Американская биографическая комедийная драма режиссёра Питера Фаррелли, вышедшая на " +
                        "экраны в 2018 году")
                .duration(130)
                .releaseDate(LocalDate.of(2018, 11, 21))
                .build());

        assertEquals(1, controller.getFilms().size());
    }

    @Test
    public void shouldPassDescriptionValidationWith200Symbols() {
        controller.create(Film.builder()
                .name("Аватар")
                .description("«Аватар: Путь воды» (англ. Avatar: The Way of Water) — американский " +
                        "научно-фантастический фильм режиссёра и сценариста Джеймса Кэмерона. Является сиквелом " +
                        "фильма «Аватар» 2009 года.Изначально премьера")
                .duration(192)
                .releaseDate(LocalDate.of(2022, 12, 6))
                .build());

        assertEquals(1, controller.getFilms().size());
    }

    @Test
    public void shouldPassReleaseDateValidation() {
        controller.create(Film.builder()
                .name("Аватар")
                .description("Путь воды")
                .duration(192)
                .releaseDate(LocalDate.of(1895, 12, 28))
                .build());

        assertEquals(1, controller.getFilms().size());
    }
}
