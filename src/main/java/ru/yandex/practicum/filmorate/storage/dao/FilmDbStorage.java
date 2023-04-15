package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("userDbStorage") UserStorage userStorage,
                         DirectorStorage directorStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
        this.directorStorage = directorStorage;
    }

    @Override
    public Map<Long, Film> getFilms() {
        Map<Long, Film> films = new HashMap<>();
        String sqlQuery = "SELECT F.*, R.RATING_NAME FROM FILM AS F JOIN RATING AS R ON F.RATING_ID = R.RATING_ID ";
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
        String queryForFilmDirector = "INSERT into FILM_DIRECTOR (film_id, DIRECTOR_ID) VALUES (?, ?)";
        if (!film.getDirectors().isEmpty()) {
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(queryForFilmDirector, film.getId(), director.getId());
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
        String queryForDeleteDirectors = "DELETE FROM FILM_DIRECTOR WHERE FILM_ID = ?";
        jdbcTemplate.update(queryForDeleteDirectors, film.getId());
        String queryForFilmDirector = "INSERT into FILM_DIRECTOR (film_id, DIRECTOR_ID) VALUES (?, ?)";
        if (!film.getDirectors().isEmpty()) {
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(queryForFilmDirector, film.getId(), director.getId());
            }
        }
        return findFilmById(film.getId());
    }

    @Override
    public Film findFilmById(long id) {
        String sqlQuery = "SELECT F.*, R.RATING_NAME FROM FILM AS F JOIN RATING AS R ON F.RATING_ID = R.RATING_ID " +
                " WHERE FILM_ID = ?;";
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
            List<Director> directors = directorStorage.getDirectorsByFilmId(film.getId());
            for (Genre genre : genresOfFilm) {
                film.getGenres().add(genre);
            }
            for (Integer like : likes) {
                film.getLikes().add(Long.valueOf(like));
            }
            for (Director director : directors) {
                film.getDirectors().add(director);
            }
            log.info("Найден фильм с id {}", id);
            return film;
        }
        log.warn("Фильм с id {} не найден", id);
        throw new FilmDoesNotExistException();
    }

    public List<Film> getFilmsByDirectorIdSortedByYearOrLikes(int id, String sortBy) {
        try {
            directorStorage.getDirectorById(id);
            String sql;
            if (sortBy.equals("year")) {
                sql = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, " +
                        "r.RATING_NAME\n FROM FILM f \n" +
                        "JOIN RATING r ON f.RATING_ID = r.RATING_ID \n" +
                        "JOIN FILM_DIRECTOR fd ON f.FILM_ID = fd.FILM_ID \n" +
                        "WHERE fd.DIRECTOR_ID = ? " +
                        "ORDER BY RELEASE_DATE ";
                return jdbcTemplate.query(sql, this::mapRowToFilm, id);
            }
            if (sortBy.equals("likes")) {
                sql = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, " +
                        "r.RATING_NAME, count(fl.USER_ID) AS likes_quantity FROM FILM f \n" +
                        "JOIN RATING r ON f.RATING_ID = r.RATING_ID \n" +
                        "JOIN FILM_DIRECTOR fd ON f.FILM_ID = fd.FILM_ID \n" +
                        "LEFT JOIN FILM_LIKE fl ON f.FILM_ID = fl.FILM_ID \n" +
                        "where fd.DIRECTOR_ID = ? " +
                        "GROUP BY f.FILM_ID \n" +
                        "ORDER BY likes_quantity desc";
                return jdbcTemplate.query(sql, this::mapRowToFilm, id);
            }
            return new ArrayList<>();
        } catch (EmptyResultDataAccessException e) {
            throw new DirectorNotFoundException();
        }
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        log.info("Film build start>>>>>");
        Film film = Film.builder()
                .id(rs.getLong("FILM_ID"))
                .name(rs.getString("NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .mpa(new Mpa(rs.getInt("RATING_ID"), rs.getString("RATING_NAME")))
                .build();
        log.info("Film = {}", film);
        List<Genre> genresOfFilm = getGenresOfFilm(film.getId());
        List<Integer> likes = getLikesOfFilm(film.getId());
        List<Director> directors = directorStorage.getDirectorsByFilmId(film.getId());
        for (Genre genre : genresOfFilm) {
            film.getGenres().add(genre);
        }
        for (Integer like : likes) {
            film.getLikes().add(Long.valueOf(like));
        }
        for (Director director : directors) {
            film.getDirectors().add(director);
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
    public List<Film> getFilmsByDirectorOrTitle(String query, String director, String title) {
        log.info("Query = {} director = {} title = {}", query, director, title);

        if (!title.isEmpty() && !director.isEmpty()) {
            log.info("Find by title or director >>>>");
            return getFilmsByDirectorOrTitle(query);
        }
        if (title.isEmpty()) {
            log.info("Find by director>>>>");
            return getFilmsByDirector(query);
        }
        log.info("Find by title>>>>");
        return getFilmsByTitle(query);
    }

    public void deleteFilm(long filmId) {
        Film film = findFilmById(filmId);
        String sqlQuery = "DELETE FROM FILM WHERE FILM_ID = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private List<Film> getFilmsByDirector(String query) {
        String sql = "SELECT f.*, r.RATING_NAME FROM FILM f INNER JOIN RATING r ON r.RATING_ID = f.RATING_ID " +
                "LEFT JOIN FILM_DIRECTOR fd ON fd.FILM_ID = f.FILM_ID LEFT JOIN DIRECTOR d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "LEFT JOIN FILM_LIKE fl ON fl.FILM_ID = f.FILM_ID GROUP BY f.FILM_ID " +
                "HAVING LOWER(d.DIRECTOR_NAME) LIKE ? ORDER BY COUNT(fl.USER_ID) DESC";
        return jdbcTemplate.query(sql, this::mapRowToFilm, query);
    }

    private List<Film> getFilmsByTitle(String query) {
        String sql = "SELECT f.*, r.RATING_NAME FROM FILM f INNER JOIN RATING r ON r.RATING_ID = f.RATING_ID " +
                "LEFT JOIN FILM_LIKE fl ON fl.FILM_ID = f.FILM_ID GROUP BY f.FILM_ID " +
                "HAVING LOWER(f.NAME) LIKE ? ORDER BY COUNT(fl.USER_ID) DESC";
        return jdbcTemplate.query(sql, this::mapRowToFilm, query);
    }

    private List<Film> getFilmsByDirectorOrTitle(String query) {
        String sql = "SELECT f.*, r.RATING_NAME FROM FILM f INNER JOIN RATING r ON r.RATING_ID = f.RATING_ID " +
                "LEFT JOIN FILM_DIRECTOR fd ON fd.FILM_ID = f.FILM_ID LEFT JOIN DIRECTOR d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "LEFT JOIN FILM_LIKE fl ON fl.FILM_ID = f.FILM_ID GROUP BY f.FILM_ID " +
                "HAVING LOWER(d.DIRECTOR_NAME) LIKE ? OR LOWER(f.NAME) LIKE ? ORDER BY COUNT(fl.USER_ID) DESC";
        return jdbcTemplate.query(sql, this::mapRowToFilm, query, query);
    }

    @Override
    public List<Film> getRecommendations(long userId) {
        /*
        Описание запроса:
        Подзапрос (1) - Найти id пользователей, с максимальным количеством пересечения по лайкам:
        Объединям таблицу FILM_LIKE с самой собой, делаем right join
        и оставляем справа только id данного пользователя, слева id всех пользователей, кроме нашего и null.
        Получается такая таблица: FL1.user_id | film_id | FL2.user_id
        Таким образом в FL1.user_id получаются id всех пользователей, которые лайкали такие же фильмы, что и данный.
        Группируем пользователей по id, сортируем по частоте этих id
        (то есть в начале списка будут пользователи, у которых наиболее совпадают лайки с данным).
        Выбираем первых трех из этих пользователей

        Подзапрос (2):
        Находим id фильмов, которые лайкнули найденные пользователи

        Подзапрос (3):
        Находим id фильмов, которые лайкнул данный пользователь

        Запрос (4):
        Находим фильмы, c id, которые есть в списке из (2), но нет в списке из (3).
        (То есть те, которые лайкали найденные пользователи, на не лайкал данный)
         */
        String sql =
                "SELECT * FROM FILM F " + //(4)
                "JOIN RATING R ON F.RATING_ID = R.RATING_ID " +
                "WHERE F.FILM_ID IN (" +
                    "SELECT FILM_ID FROM FILM_LIKE " + //(2)
                    "WHERE USER_ID IN (" +
                        "SELECT FL1.USER_ID FROM FILM_LIKE FL1 " + //(1)
                        "RIGHT JOIN FILM_LIKE FL2 ON FL2.FILM_ID = FL1.FILM_ID " +
                        "GROUP BY FL1.USER_ID, FL2.USER_ID " +
                        "HAVING FL1.USER_ID IS NOT NULL AND " +
                            "FL1.USER_ID != ? AND " +
                            "FL2.USER_ID = ? " +
                        "ORDER BY COUNT(FL1.USER_ID) DESC " +
                        "LIMIT 3 " +
                    ") " +
                    "AND FILM_ID NOT IN (" +
                        "SELECT FILM_ID FROM FILM_LIKE " + //(3)
                        "WHERE USER_ID = ?" +
                    ")" +
                ")";

        return jdbcTemplate.query(sql, this::mapRowToFilm, userId, userId, userId);
    }
}

