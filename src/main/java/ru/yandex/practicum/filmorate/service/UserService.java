package ru.yandex.practicum.filmorate.service;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    private long nextId = 1;
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        return List.copyOf(userStorage.getUsers().values());
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
        user.setId(getNextId());
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
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        if (user != null && friend != null) {
            user.getFriends().add(friendId);
            friend.getFriends().add(userId);
            log.info("Пользователи {} и {} теперь друзья", user, friend);
        }
    }

    public void removeFromFriends(long userId, long friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        user.getFriends().remove(friend);
        friend.getFriends().remove(user);
        log.info("Пользователи {} и {} теперь не являются друзьями", user, friend);
    }

    public List<User> getMutualFriends(long userId, long otherUserId) {
        List<User> mutualFriends = new ArrayList<>();
        User user = findUserById(userId);
        User otherUser = findUserById(otherUserId);
        Set<Long> mutualFriendsIds = Sets.intersection(user.getFriends(), otherUser.getFriends());
        for (Long id : mutualFriendsIds) {
            mutualFriends.add(findUserById(id));
        }
        return mutualFriends;
    }

    public List<User> getAllFriends(long userId) {
        List<User> friends = new ArrayList<>();
        User user = findUserById(userId);
        for (Long id : user.getFriends()) {
            friends.add(findUserById(id));
        }
        return friends;
    }

    private long getNextId() {
        return nextId++;
    }
}
