package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private int nextId = 1;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Director> getAllDirectors() {
        String sql = "SELECT * FROM DIRECTOR;";
        return jdbcTemplate.query(sql, this::mapRowToDirector);
    }

    public Director getDirectorById(int id) {
        try {
            String sql = "SELECT * FROM DIRECTOR WHERE DIRECTOR_ID = ?";
            return jdbcTemplate.queryForObject(sql, this::mapRowToDirector, id);
        } catch (EmptyResultDataAccessException e) {
            throw new DirectorNotFoundException("Режиссер не найден");
        }
    }

    public Director addDirector(Director director) {
        String sql = "INSERT INTO PUBLIC.DIRECTOR (DIRECTOR_ID, DIRECTOR_NAME) VALUES (?, ?)";
        director.setId(nextId++);
        jdbcTemplate.update(sql, director.getId(), director.getName());
        return director;
    }

    public Director updateDirector(Director director) {
        try {
            Director dir = getDirectorById(director.getId());
            String sql = "UPDATE DIRECTOR SET DIRECTOR_NAME = ? WHERE DIRECTOR_ID = ?";
            jdbcTemplate.update(sql, director.getName(), director.getId());
            return director;
        } catch (EmptyResultDataAccessException e) {
            throw new DirectorNotFoundException("Режиссер не найден");
        }
    }

    public void deleteDirectorById(int id) {
        try {
            String sql = "DELETE DIRECTOR WHERE DIRECTOR_ID = ?";
            jdbcTemplate.update(sql, id);
        } catch (EmptyResultDataAccessException e) {
            throw new DirectorNotFoundException("Режиссер не найден");
        }
    }

    public List<Director> getDirectorsByFilmId(long filmId) {
        String queryForDirectors = "SELECT d.DIRECTOR_ID, d.DIRECTOR_NAME " +
                "FROM FILM_DIRECTOR fd JOIN DIRECTOR d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "WHERE fd.FILM_ID = ?";
        return jdbcTemplate.query(queryForDirectors, this::mapRowToDirector, filmId);
    }

    private Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        return new Director(rs.getInt("DIRECTOR_ID"), rs.getString("DIRECTOR_NAME"));
    }


}
