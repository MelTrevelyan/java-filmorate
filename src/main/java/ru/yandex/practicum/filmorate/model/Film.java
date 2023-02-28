package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {

    private final Set<Long> likes = new HashSet<>();
    private Long id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final Integer duration;
}
