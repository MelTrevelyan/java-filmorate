package ru.yandex.practicum.filmorate.service;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void addFriend(long userId, long friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFromFriends(long userId, long friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        user.getFriends().remove(friend);
        friend.getFriends().remove(user);
    }

    public List<User> getMutualFriends(long userId, long otherUserId) {
        List<User> mutualFriends = new ArrayList<>();
        User user = userStorage.findUserById(userId);
        User otherUser = userStorage.findUserById(otherUserId);
        Set<Long> mutualFriendsIds = Sets.intersection(user.getFriends(), otherUser.getFriends());
        for (Long id : mutualFriendsIds) {
            mutualFriends.add(userStorage.findUserById(id));
        }
        return mutualFriends;
    }
}
