package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public Map<Long, Film> getFilms() {
        Map<Long, Film> films = new HashMap<>();
        String sqlQuery = "SELECT * FROM FILM AS F JOIN RATING AS R ON F.RATING_ID = R.RATING_ID;";
        List<Film> filmsFromDb = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        for (Film film : filmsFromDb) {
            films.put(film.getId(), film);
        }
        return films;
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = "INSERT INTO FILM (NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) " +
                "VALUES (?, ?, ?, ?, ?);";
        String queryForFilmGenre = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId());
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(queryForFilmGenre, film.getId(), genre.getId());
            }
        }
        return findFilmById(film.getId());
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE FILM SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, RATING_ID = ?, DURATION = ?" +
                " WHERE FILM_ID = ?;";
        String queryToDeleteFilmGenres = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?;";
        String queryForUpdateGenre = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getMpa().getId(), film.getDuration(), film.getId());
        jdbcTemplate.update(queryToDeleteFilmGenres, film.getId());
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(queryForUpdateGenre, film.getId(), genre.getId());
            }
        }
        return findFilmById(film.getId());
    }

    @Override
    public Film findFilmById(long id) {
        String sqlQuery = "SELECT * FROM FILM AS F JOIN RATING AS R ON F.RATING_ID = R.RATING_ID " +
                "WHERE FILM_ID = ?;";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (filmRows.next()) {
            Film film = Film.builder()
                    .id(filmRows.getLong("FILM_ID"))
                    .name(filmRows.getString("NAME"))
                    .description(filmRows.getString("DESCRIPTION"))
                    .releaseDate(Objects.requireNonNull(filmRows.getDate("RELEASE_DATE")).toLocalDate())
                    .duration(filmRows.getInt("DURATION"))
                    .mpa(new Mpa(filmRows.getInt("RATING_ID"), filmRows.getString("RATING_NAME")))
                    .build();
            List<Genre> genresOfFilm = getGenresOfFilm(id);
            List<Integer> likes = getLikesOfFilm(film.getId());
            for (Genre genre : genresOfFilm) {
                film.getGenres().add(genre);
            }
            for (Integer like : likes) {
                film.getLikes().add(Long.valueOf(like));
            }
            log.info("Найден фильм с id {}", id);
            return film;
        }
        log.warn("Фильм с id {} не найден", id);
        throw new FilmDoesNotExistException();
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong("FILM_ID"))
                .name(rs.getString("NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .mpa(new Mpa(rs.getInt("RATING_ID"), rs.getString("RATING_NAME")))
                .build();
        List<Genre> genresOfFilm = getGenresOfFilm(film.getId());
        List<Integer> likes = getLikesOfFilm(film.getId());
        for (Genre genre : genresOfFilm) {
            film.getGenres().add(genre);
        }
        for (Integer like : likes) {
            film.getLikes().add(Long.valueOf(like));
        }

        return film;
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE_NAME"));
    }

    private Integer mapRowToLike(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("USER_ID");
    }

    private List<Genre> getGenresOfFilm(long filmId) {
        String queryForFilmGenres = "SELECT FG.FILM_ID, FG.GENRE_ID, G.GENRE_NAME FROM FILM_GENRE FG" +
                " JOIN GENRE G ON G.GENRE_ID = FG.GENRE_ID WHERE FILM_ID = ?;";
        return jdbcTemplate.query(queryForFilmGenres, this::mapRowToGenre, filmId);
    }

    private List<Integer> getLikesOfFilm(long filmId) {
        String queryForFilmLikes = "SELECT USER_ID FROM FILM_LIKE WHERE FILM_ID = ?;";
        return jdbcTemplate.query(queryForFilmLikes, this::mapRowToLike, filmId);
    }

    @Override
    public void addLike(long filmId, long userId) {
        Film film = findFilmById(filmId);
        User user = userStorage.findUserById(userId);
        String sqlQuery = "INSERT INTO FILM_LIKE (FILM_ID, USER_ID) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        Film film = findFilmById(filmId);
        User user = userStorage.findUserById(userId);
        String sqlQuery = "DELETE FROM FILM_LIKE WHERE FILM_ID = ? AND USER_ID = ?;";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteFilm(long filmId) {
        Film film = findFilmById(filmId);
        String sqlQuery = "DELETE FROM FILM WHERE FILM_ID = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
    }
}

