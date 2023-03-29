package ru.yandex.practicum.filmorate.storage.inmemory;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }

    @Override
    public User create(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUserById(long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        return null;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        if (user != null && friend != null) {
            user.getFriends().add(friendId);
            friend.getFriends().add(userId);
        }
    }

    @Override
    public void removeFromFriends(long userId, long friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        user.getFriends().remove(friend);
        friend.getFriends().remove(user);
    }

    @Override
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

    @Override
    public List<User> getAllFriends(long userId) {
        List<User> friends = new ArrayList<>();
        User user = findUserById(userId);
        for (Long id : user.getFriends()) {
            friends.add(findUserById(id));
        }
        return friends;
    }
}
