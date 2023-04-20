package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;


    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, @Qualifier("filmDbStorage") FilmStorage filmStorage,
                       EventStorage eventStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventStorage = eventStorage;
    }

    public Collection<User> getUsers() {
        return Collections.unmodifiableCollection(userStorage.getUsers().values());
    }

    public User create(User user) {
        for (User registeredUser : userStorage.getUsers().values()) {
            if (registeredUser.getEmail().equals(user.getEmail())) {
                log.warn("Пользователь с электронной почтой " + user.getEmail()
                        + " уже зарегистрирован");
                throw new ValidationException();
            }
        }
        UserValidator.validateUser(user);
        log.info("Добавлен новый пользователь");
        return userStorage.create(user);
    }

    public User update(User user) {
        UserValidator.validateUser(user);
        if (userStorage.findUserById(user.getId()) == null) {
            log.warn("Невозможно обновить пользователя");
            throw new UserDoesNotExistException();
        }
        log.info("Пользователь с id {} обновлён", user.getId());
        return userStorage.update(user);
    }

    public User findUserById(long id) {
        User user = userStorage.findUserById(id);
        if (user == null) {
            log.warn("Пользователя с id {} не найдено", id);
            throw new UserDoesNotExistException();
        }
        return user;
    }

    public void addFriend(long userId, long friendId) {
        userStorage.addFriend(userId, friendId);
        Event event = new Event(userId, EventType.FRIEND, EventOperation.ADD, friendId);
        eventStorage.addEvent(event);
        log.info("Пользователи с id {} и {} теперь друзья", userId, friendId);
    }

    public void removeFromFriends(long userId, long friendId) {
        userStorage.removeFromFriends(userId, friendId);
        Event event = new Event(userId, EventType.FRIEND, EventOperation.REMOVE, friendId);
        eventStorage.addEvent(event);
        log.info("Пользователи с id {} и {} теперь не являются друзьями", userId, friendId);
    }

    public List<User> getMutualFriends(long userId, long otherUserId) {
        return userStorage.getMutualFriends(userId, otherUserId);
    }

    public List<User> getAllFriends(long userId) {
        return userStorage.getAllFriends(userId);
    }

    public void deleteUser(long userId) {
        userStorage.deleteUser(userId);
    }

    public List<Film> getRecommendations(long userId) {
        findUserById(userId);
        return filmStorage.getRecommendations(userId);
    }
}
