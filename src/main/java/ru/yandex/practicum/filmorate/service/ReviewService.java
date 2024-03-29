package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Slf4j
@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final EventService eventService;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, EventService eventService) {
        this.reviewStorage = reviewStorage;
        this.eventService = eventService;
    }

    public Review create(Review review) {
        Review review1 = reviewStorage.create(review);
        Event event = new Event(review1.getUserId(), EventType.REVIEW, EventOperation.ADD, review1.getReviewId());
        eventService.addEvent(event);
        return review1;
    }

    public Review update(Review review) {
        Review review1 = reviewStorage.update(review);
        Event event = new Event(review1.getUserId(), EventType.REVIEW, EventOperation.UPDATE, review1.getReviewId());
        eventService.addEvent(event);
        return review1;
    }

    public void deleteReview(long id) {
        long userId = reviewStorage.findReviewById(id).getUserId();
        Event event = new Event(userId, EventType.REVIEW, EventOperation.REMOVE, id);
        eventService.addEvent(event);
        reviewStorage.deleteReview(id);
    }

    public Review findReviewById(long id) {
        return reviewStorage.findReviewById(id);
    }

    public List<Review> getReviewsOfFilm(long filmId, int count) {
        return reviewStorage.getReviewsOfFilm(filmId, count);
    }

    public void addLike(long reviewId, long userId) {
        reviewStorage.addLike(reviewId, userId);
    }

    public void addDislike(long reviewId, long userId) {
        reviewStorage.addDislike(reviewId, userId);
    }

    public void deleteLike(long reviewId, long userId) {
        reviewStorage.deleteLike(reviewId, userId);
    }

    public void deleteDislike(long reviewId, long userId) {
        reviewStorage.deleteDislike(reviewId, userId);
    }
}
