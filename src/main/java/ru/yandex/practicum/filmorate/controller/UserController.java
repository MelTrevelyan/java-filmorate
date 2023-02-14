package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private int nextId = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        for (User registeredUser : users.values()) {
            if (registeredUser.getEmail().equals(user.getEmail())) {
                throw new ValidationException("Пользователь с электронной почтой " + user.getEmail()
                        + " уже зарегистрирован");
            }
        }
        validateUser(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь");
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        validateUser(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Невозможно обновить пользователя");
        }
        users.put(user.getId(), user);
        log.info("Пользователь с id " + user.getId() + " обновлён");
        return user;
    }

    private int getNextId() {
        return nextId++;
    }

    private void validateUser(User user) {
        validateEmail(user.getEmail());
        validateLogin(user.getLogin());
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        validateBirthday(user.getBirthday());
    }

    private void validateEmail(String email) {
        if (email == null || !email.contains("@") || email.isBlank()) {
            throw new ValidationException("Некорректный адрес электронной почты");
        }
    }

    private void validateLogin(String login) {
        if (login == null || login.isBlank() || login.contains(" ")) {
            throw new ValidationException("Некорректный логин");
        }
    }

    private void validateBirthday(LocalDate birthday) {
        if (birthday == null || birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
