package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final EventService eventService;

    @Autowired
    public UserController(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User findUserById(@NotNull @PathVariable long id) {
        return userService.findUserById(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addFriend(@NotNull @PathVariable long id, @NotNull @PathVariable long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void removeFromFriends(@NotNull @PathVariable long id, @NotNull @PathVariable long friendId) {
        userService.removeFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@NotNull @PathVariable long id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@NotNull @PathVariable long id, @NotNull @PathVariable long otherId) {
        return userService.getMutualFriends(id, otherId);
    }

    @DeleteMapping(value = "/{userId}")
    public void deleteUser(@NotNull @PathVariable long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/{userId}/recommendations")
    public List<Film> getRecommendations(@NotNull @PathVariable long userId) {
        return userService.getRecommendations(userId);
    }

    @GetMapping("/{userId}/feed")
    public List<Event> getUserFeed(@NotNull @PathVariable long userId) {
        return eventService.findUserEvent(userId);
    }
}

