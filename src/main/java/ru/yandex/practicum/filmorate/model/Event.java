package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class Event {
    @JsonProperty("timestamp")
    private long timeAdd;
    private long userId;
    private EventType eventType;
    @JsonProperty("operation")
    private EventOperation eventOperation;
    @JsonProperty("eventId")
    private long id;
    private long entityId;

    public Event(long userId, EventType eventType, EventOperation eventOperation,long entityId) {
        this.timeAdd = new Timestamp(System.currentTimeMillis()).getTime();
        this.userId = userId;
        this.eventType = eventType;
        this.eventOperation = eventOperation;
        this.id = id;
        this.entityId = entityId;
    }
}

