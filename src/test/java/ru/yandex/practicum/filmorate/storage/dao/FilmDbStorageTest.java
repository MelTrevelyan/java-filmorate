package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    @Test
    void getRecommendations() {
        userDbStorage.create(User.builder()
                .id(1L)
                .login("USER_ONE")
                .name("Melissa")
                .email("voiceemail@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build());

        userDbStorage.create(User.builder()
                .id(2L)
                .login("USER_TWO")
                .name("Melissa")
                .email("voiceemail@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build());

        filmDbStorage.create(Film.builder().id(1L).name("FILM_ONE").description("").duration(140)
                .releaseDate(LocalDate.now()).mpa(new Mpa(1, "")).build());
        filmDbStorage.create(Film.builder().id(2L).name("FILM_TWO").description("").duration(140)
                .releaseDate(LocalDate.now()).mpa(new Mpa(1, "")).build());

        filmDbStorage.addLike(1, 1);
        filmDbStorage.addLike(2, 1);
        filmDbStorage.addLike(1, 2);

        List<Film> films = filmDbStorage.getRecommendations(2);
        assertEquals(1, films.size());
    }
}