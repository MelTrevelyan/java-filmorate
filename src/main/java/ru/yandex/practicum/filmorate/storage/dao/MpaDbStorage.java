package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Integer, Mpa> getAllMpa() {
        Map<Integer, Mpa> allMpa = new HashMap<>();
        String sqlQuery = "SELECT * FROM RATING;";
        List<Mpa> mpaFromDb = jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
        for (Mpa mpa : mpaFromDb) {
            allMpa.put(mpa.getId(), mpa);
        }
        return allMpa;
    }

    @Override
    public Optional<Mpa> findMpaById(Integer id) {
        String sqlQuery = "SELECT * FROM RATING WHERE ID = ?"; //RATING_ID изменено на ID, т.к. при запросе JOIN ошибка с одинаковыми столбцами
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (mpaRows.next()) {
            Mpa mpa = new Mpa(mpaRows.getInt("ID"), mpaRows.getString("RATING_NAME"));
            log.info("Найден рейтинг с id {}", id);
            return Optional.of(mpa);
        }
        log.warn("Рейтинг с id {} не найден", id);
        return Optional.empty();
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("ID"), rs.getString("RATING_NAME"));
    }
}
