package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
@Builder
public class Director {
    private int id;
    @NotBlank(message = "Имя режиссера не должно быть пустым")
    private final String name;

    public Director(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
