package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, User> getUsers() {
        Map<Long, User> users = new HashMap<>();
        String sqlQuery = "SELECT * FROM \"USER\"";
        List<User> usersFromDb = jdbcTemplate.query(sqlQuery, this::mapRowToUser);
        for (User user : usersFromDb) {
            users.put(user.getId(), user);
        }
        return users;
    }

    @Override
    public User create(User user) {
        String sqlQuery = "INSERT INTO \"USER\" (EMAIL, LOGIN, BIRTHDAY, NAME) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getBirthday(), user.getName());
        return user;
    }

    @Override
    public User update(User user) {
        String SqlQuery = "UPDATE \"USER\" SET EMAIL = ?, LOGIN = ?, BIRTHDAY = ?, NAME = ? WHERE USER_ID = ?";
        jdbcTemplate.update(SqlQuery, user.getEmail(), user.getLogin(), user.getBirthday(), user.getName(),
                user.getId());
        return user;
    }

    @Override
    public User findUserById(long id) {
        String sqlQuery = "SELECT * FROM \"USER\" WHERE USER_ID = ?";
        SqlRowSet userRows =jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRows.next()) {
            User user = User.builder()
                    .email(userRows.getString("EMAIL"))
                    .login(userRows.getString("LOGIN"))
                    .name(userRows.getString("NAME"))
                    .id(userRows.getLong("USER_ID"))
                    .birthday((Objects.requireNonNull(userRows.getDate("BIRTHDAY"))).toLocalDate())
                    .build();
            log.info("Найден пользователь с id {}", id);
            return user;
        }
        log.warn("Пользователь с id {} не найден", id);
        throw new UserDoesNotExistException();
    }

    private User mapRowToUser(ResultSet rs, int rowNum) {
        try {
            return User.builder()
                    .email(rs.getString("EMAIL"))
                    .login(rs.getString("LOGIN"))
                    .name(rs.getString("NAME"))
                    .id(rs.getLong("USER_ID"))
                    .birthday((rs.getDate("BIRTHDAY")).toLocalDate())
                    .build();
        } catch (SQLException e) {
            throw new UserDoesNotExistException();
        }
    }
}

