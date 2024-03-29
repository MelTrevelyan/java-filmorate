package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@Validated
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteReview(@NotNull @PathVariable long id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review findReviewById(@NotNull @PathVariable long id) {
        return reviewService.findReviewById(id);
    }

    @GetMapping
    public List<Review> getReviewsOfFilm(@RequestParam(defaultValue = "0") long filmId,
                                         @RequestParam(defaultValue = "10") int count) {
        return reviewService.getReviewsOfFilm(filmId, count);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addLike(@NotNull @PathVariable long id, @NotNull @PathVariable long userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping(value = "/{id}/dislike/{userId}")
    public void addDislike(@NotNull @PathVariable long id, @NotNull @PathVariable long userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteLike(@NotNull @PathVariable long id, @NotNull @PathVariable long userId) {
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping(value = "/{id}/dislike/{userId}")
    public void deleteDislike(@NotNull @PathVariable long id, @NotNull @PathVariable long userId) {
        reviewService.deleteDislike(id, userId);
    }
}
