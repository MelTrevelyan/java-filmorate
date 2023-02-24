package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private int nextId = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User create(User user) {
        for (User registeredUser : users.values()) {
            if (registeredUser.getEmail().equals(user.getEmail())) {
                log.warn("Пользователь с электронной почтой " + user.getEmail()
                        + " уже зарегистрирован");
                throw new ValidationException();
            }
        }
        UserValidator.validateUser(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь");
        return user;
    }

    @Override
    public User update(User user) {
        UserValidator.validateUser(user);
        if (!users.containsKey(user.getId())) {
            log.warn("Невозможно обновить пользователя");
            throw new ValidationException();
        }
        users.put(user.getId(), user);
        log.info("Пользователь с id " + user.getId() + " обновлён");
        return user;
    }

    private int getNextId() {
        return nextId++;
    }
}
