package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ReviewDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final String QUERY_TO_SET_USEFUL = "UPDATE REVIEW SET USEFUL = ((SELECT COUNT(REVIEW_ID) " +
            "FROM REVIEW_LIKE WHERE REVIEW_ID = ?) - (SELECT COUNT(REVIEW_ID) FROM REVIEW_DISLIKE " +
            "WHERE REVIEW_ID = ?)) WHERE REVIEW_ID = ?;";

    public ReviewDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("filmDbStorage") FilmStorage filmStorage,
                           @Qualifier("userDbStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Review create(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        User user = userStorage.findUserById(review.getUserId());
        Film film = filmStorage.findFilmById(review.getFilmId());
        String sqlQuery = "INSERT INTO REVIEW (CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL) VALUES " +
                "(?, ?, ?, ?, ?);";
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con
                    .prepareStatement(sqlQuery, new String[]{"review_id"});
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            ps.setLong(5, review.getUseful());
            return ps;
        }, keyHolder);
        review.setReviewId((Objects.requireNonNull(keyHolder.getKey()).longValue()));
        setRightUseful(review.getReviewId());
        return findReviewById(review.getReviewId());
    }

    @Override
    public Review update(Review review) {
        String sqlQuery = "UPDATE REVIEW SET CONTENT = ?, IS_POSITIVE = ? WHERE REVIEW_ID = ?;";
        jdbcTemplate.update(sqlQuery, review.getContent(), review.getIsPositive(), review.getReviewId());
        return findReviewById(review.getReviewId());
    }

    @Override
    public void deleteReview(long id) {
        String sqlQuery = "DELETE FROM REVIEW WHERE REVIEW_ID = ?;";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Review findReviewById(long id) {
        String sqlQuery = "SELECT * FROM REVIEW WHERE REVIEW_ID = ?;";
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (reviewRows.next()) {
            Review review = Review.builder()
                    .reviewId(reviewRows.getLong("REVIEW_ID"))
                    .content(reviewRows.getString("CONTENT"))
                    .isPositive(reviewRows.getBoolean("IS_POSITIVE"))
                    .userId(reviewRows.getLong("USER_ID"))
                    .filmId(reviewRows.getLong("FILM_ID"))
                    .useful(reviewRows.getLong("USEFUL"))
                    .build();
            log.info("Найден отзыв с id {}", id);
            return review;
        }
        log.warn("Отзыв с id {} не найден", id);
        throw new ReviewDoesNotExistException();
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("REVIEW_ID"))
                .content(rs.getString("CONTENT"))
                .isPositive(rs.getBoolean("IS_POSITIVE"))
                .userId(rs.getLong("USER_ID"))
                .filmId(rs.getLong("FILM_ID"))
                .useful(rs.getLong("USEFUL"))
                .build();
    }

    @Override
    public List<Review> getReviewsOfFilm(long filmId, int count) {
        String queryWithFilmId = "SELECT * FROM REVIEW WHERE FILM_ID = ? ORDER BY USEFUL DESC LIMIT ?";
        String queryWithoutFilmId = "SELECT * FROM REVIEW ORDER BY USEFUL DESC LIMIT ?";
        if (filmId == 0) {
            return jdbcTemplate.query(queryWithoutFilmId, this::mapRowToReview, count);
        }
        return jdbcTemplate.query(queryWithFilmId, this::mapRowToReview, filmId, count);
    }

    @Override
    public void addLike(long reviewId, long userId) {
        Review review = findReviewById(reviewId);
        User user = userStorage.findUserById(userId);
        String queryToAddLike = "INSERT INTO REVIEW_LIKE (REVIEW_ID, USER_ID) VALUES (?, ?);";
        jdbcTemplate.update(queryToAddLike, reviewId, userId);
        setRightUseful(reviewId);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        Review review = findReviewById(reviewId);
        User user = userStorage.findUserById(userId);
        String queryToAddDislike = "INSERT INTO REVIEW_DISLIKE (REVIEW_ID, USER_ID) VALUES (?, ?);";
        jdbcTemplate.update(queryToAddDislike, reviewId, userId);
        setRightUseful(reviewId);
    }

    @Override
    public void deleteLike(long reviewId, long userId) {
        Review review = findReviewById(reviewId);
        User user = userStorage.findUserById(userId);
        String queryToDeleteLike = "DELETE FROM REVIEW_LIKE WHERE REVIEW_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(queryToDeleteLike, reviewId, userId);
        setRightUseful(reviewId);
    }

    @Override
    public void deleteDislike(long reviewId, long userId) {
        Review review = findReviewById(reviewId);
        User user = userStorage.findUserById(userId);
        String queryToDeleteDislike = "DELETE FROM REVIEW_DISLIKE WHERE REVIEW_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(queryToDeleteDislike, reviewId, userId);
        setRightUseful(reviewId);
    }

    private void setRightUseful(long reviewId) {
        jdbcTemplate.update(QUERY_TO_SET_USEFUL, reviewId, reviewId, reviewId);
    }
}
