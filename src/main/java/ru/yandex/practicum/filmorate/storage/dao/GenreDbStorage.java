package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Integer, Genre> getAllGenres() {
        Map<Integer, Genre> allGenre = new HashMap<>();
        String sqlQuery = "SELECT * FROM GENRE;";
        List<Genre> genreFromDb = jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
        for (Genre genre : genreFromDb) {
            allGenre.put(genre.getId(), genre);
        }
        return allGenre;
    }

    @Override
    public Genre findGenreById(Integer id) {
        String sqlQuery = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (genreRows.next()) {
            Genre genre = new Genre(genreRows.getInt("GENRE_ID"),
                    genreRows.getString("GENRE_NAME"));
            log.info("Найден жанр с id {}", id);
            return genre;
        }
        log.warn("Жанр с id {} не найден", id);
        throw new GenreDoesNotExistException();
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE_NAME"));
    }
}