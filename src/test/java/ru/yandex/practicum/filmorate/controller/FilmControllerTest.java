package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerTest {
    private final FilmService filmService;
    private final UserService userService;
    private final DirectorService directorService;
    private static Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }


    @Test
    public void shouldCreateFilm() {

        Film film = Film.builder()
                .name("Акварарк")
                .description("Путь воды")
                .duration(192)
                .releaseDate(LocalDate.of(2022, 12, 6))
                .mpa(new Mpa(1, "G"))
                .build();
        filmService.create(film);

        assertTrue(filmService.getFilms().contains(film));
    }

    @Test
    public void shouldNotPassNameValidation() {
        Film film = Film.builder()
                .name("")
                .description("Уже не путь воды")
                .duration(192)
                .releaseDate(LocalDate.of(2022, 12, 6))
                .mpa(new Mpa(1, "PG"))
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
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
                .mpa(new Mpa(1, "PG"))
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldNotPassReleaseDateValidationInThePast() {
        Film film1 = Film.builder()
                .name("Аватар")
                .description("Путь воды")
                .duration(192)
                .releaseDate(LocalDate.of(1722, 12, 6))
                .mpa(new Mpa(1, "PG"))
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film1);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldPassReleaseDateValidationInTheFuture() {
        Film film = Film.builder()
                .name("Аватар")
                .description("Путь воды")
                .duration(192)
                .releaseDate(LocalDate.of(2025, 12, 6))
                .mpa(new Mpa(1, "PG"))
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
    }

    @Test
    public void shouldNotPassDurationValidation() {
        Film film = Film.builder()
                .name("Аватар")
                .description("Путь воды")
                .duration(-192)
                .releaseDate(LocalDate.of(2022, 12, 6))
                .mpa(new Mpa(1, "PG"))
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldUpdateFilm() {

        Film film = Film.builder()
                .name("Аватар")
                .description("Путь воды")
                .duration(192)
                .releaseDate(LocalDate.of(2022, 12, 6))
                .mpa(new Mpa(1, "PG"))
                .build();
        filmService.create(film);

        Film filmUpdate = Film.builder()
                .id(film.getId())
                .name("Зелёная книга")
                .description("Американская биографическая комедийная драма режиссёра Питера Фаррелли, вышедшая на " +
                        "экраны в 2018 году")
                .duration(130)
                .releaseDate(LocalDate.of(2018, 11, 21))
                .mpa(new Mpa(1, "G"))
                .build();
        filmService.update(filmUpdate);

        assertEquals(filmUpdate, filmService.findFilmById(film.getId()));
    }

    @Test
    public void shouldPassDescriptionValidationWith200Symbols() {
        Film film = Film.builder()
                .name("Аватар")
                .description("«Аватар: Путь воды» (англ. Avatar: The Way of Water) — американский " +
                        "научно-фантастический фильм режиссёра и сценариста Джеймса Кэмерона. Является сиквелом " +
                        "фильма «Аватар» 2009 года.Изначально премьера")
                .duration(192)
                .releaseDate(LocalDate.of(2022, 12, 6))
                .mpa(new Mpa(1, "G"))
                .build();

        filmService.create(film);

        assertTrue(filmService.getFilms().contains(film));
    }

    @Test
    public void shouldPassReleaseDateValidation() {
        Film film = Film.builder()
                .name("Аватар")
                .description("Путь воды")
                .duration(192)
                .releaseDate(LocalDate.of(1895, 12, 28))
                .mpa(new Mpa(1, "G"))
                .build();
        filmService.create(film);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
    }

    @Test
    public void shouldFindFilmById() {
        Film film = Film.builder()
                .name("Аватар 2")
                .description("Путь воды")
                .duration(192)
                .releaseDate(LocalDate.of(2022, 12, 6))
                .mpa(new Mpa(1, "G"))
                .build();
        filmService.create(film);

        assertEquals(film, filmService.findFilmById(film.getId()));
    }

    @Test
    public void shouldAddLike() {
        User user = User.builder()
                .login("Sweet")
                .name("Melissa")
                .email("Sweet@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(user);

        Film film = Film.builder()
                .name("Форрест Гамп1")
                .description("Жизнь как коробка конфет")
                .duration(192)
                .releaseDate(LocalDate.of(1981, 12, 6))
                .mpa(new Mpa(1, "PG"))
                .build();
        filmService.create(film);

        filmService.addLike(film.getId(), user.getId());

        assertEquals(1, filmService.findFilmById(film.getId()).getLikes().size());
    }

    @Test
    public void shouldDeleteLike() {
        User user = User.builder()
                .login("Iris")
                .name("Melissa")
                .email("willow@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(user);

        Film film = Film.builder()
                .name("Форрест беги")
                .description("Жизнь как коробка конфет")
                .duration(192)
                .releaseDate(LocalDate.of(1981, 12, 6))
                .mpa(new Mpa(1, "PG"))
                .build();
        filmService.create(film);

        filmService.addLike(film.getId(), user.getId());
        filmService.deleteLike(film.getId(), user.getId());

        assertEquals(0, film.getLikes().size());
    }

    @Test
    public void shouldGetMostPopularFilms() {
        User user = User.builder()
                .login("Iris")
                .name("Melissa")
                .email("mellow@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(user);

        User secondUser = User.builder()
                .login("Iris")
                .name("Melissa")
                .email("meow@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(secondUser);

        Film film = Film.builder()
                .name("Форрест run")
                .description("Жизнь как коробка конфет")
                .duration(192)
                .releaseDate(LocalDate.of(1981, 12, 6))
                .mpa(new Mpa(1, "PG"))
                .build();
        filmService.create(film);

        filmService.addLike(film.getId(), user.getId());
        filmService.addLike(film.getId(), secondUser.getId());

        assertEquals(List.of(filmService.findFilmById(film.getId())), filmService.getMostPopularFilms(1));
    }

    @Test
    public void getFilmsByDirectorOrTitle() {
        Director director = Director.builder().name("Гай Ричи").build();
        directorService.addDirector(director);
        User user = User.builder()
                .login("Iris")
                .name("Melissa")
                .email("mello@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(user);


        Film film = Film.builder()
                .name("Джентльмены")
                .description("Наркобарон хочет уйти на покой, но криминальный мир не отпускает. " +
                        "Успешное возвращение Гая Ричи к корням")
                .duration(192)
                .releaseDate(LocalDate.of(2022, 3, 7))
                .mpa(new Mpa(1, "PG"))
                .build();
        film.getDirectors().add(director);
        filmService.create(film);

        filmService.addLike(film.getId(), user.getId());

        assertEquals("Джентльмены", filmService.getFilmsByDirectorOrTitle("Джен", "title")
                .get(0).getName());
        assertEquals("Джентльмены", filmService.getFilmsByDirectorOrTitle("Га", "director")
                .get(0).getName());
        assertEquals("Форрест Гамп1", filmService.getFilmsByDirectorOrTitle("Га", "director, title")
                .get(0).getName());
        directorService.deleteDirectorById(director.getId());
    }

    @Test
    public void shouldDeleteFilm() {
        Film film = Film.builder()
                .name("Век Адалин")
                .description("Бессмертие от удара молнии")
                .duration(192)
                .releaseDate(LocalDate.of(2010, 12, 6))
                .mpa(new Mpa(1, "PG"))
                .build();
        filmService.create(film);
        filmService.deleteFilm(film.getId());

        assertFalse(filmService.getFilms().contains(film));
    }
}