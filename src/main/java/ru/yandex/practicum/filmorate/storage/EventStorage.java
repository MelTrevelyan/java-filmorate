package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    void addEvent(long userId, int eventTypeId, int eventOperationId, long entityId);

    List<Event> findUserEvent(Long userId);
}
