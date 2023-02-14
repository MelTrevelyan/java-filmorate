package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {

    private UserController controller;

    @BeforeEach
    public void beforeEach() {
        controller = new UserController();
    }

    @Test
    public void shouldPassValidation() {
        controller.create(User.builder()
                .login("Mango11")
                .name("Melissa")
                .email("nicemail@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build());

        assertEquals(1, controller.getUsers().size());
    }

    @Test
    public void shouldNotPassEmailValidation() {
        User user1 = User.builder()
                .login("Mango11")
                .name("Melissa")
                .email("nicemailmail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        User user2 = User.builder()
                .login("Mango11")
                .name("Melissa")
                .email("")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        assertThrows(ValidationException.class, () -> controller.create(user1));
        assertThrows(ValidationException.class, () -> controller.create(user2));
    }

    @Test
    public void shouldNotPassLoginValidation() {
        User user1 = User.builder()
                .login("")
                .name("Melissa")
                .email("nicemail@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        User user2 = User.builder()
                .login(" Mango 11")
                .name("Melissa")
                .email("nicemail@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        assertThrows(ValidationException.class, () -> controller.create(user1));
        assertThrows(ValidationException.class, () -> controller.create(user2));
    }

    @Test
    public void shouldNotPassBirthdayValidation() {
        User user = User.builder()
                .login("Mango11")
                .name("Melissa")
                .email("nicemail@mail.ru")
                .birthday(LocalDate.of(3000, 8, 15))
                .build();

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    public void emptyUserShouldNotPassValidation() {
        User user = User.builder().build();

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    public void shouldUpdateUser() {
        controller.create(User.builder()
                .login("Mango11")
                .name("Melissa")
                .email("nicemail@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build());

        controller.update(User.builder()
                .id(1)
                .login("Mango22")
                .name("Anna")
                .email("palmtree@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build());

        assertEquals(1, controller.getUsers().size());
    }

    @Test
    public void shouldCreateUserWithEmptyName() {
        User user = User.builder()
                .login("Mango11")
                .email("nicemail@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        controller.create(user);

        assertEquals("Mango11", user.getName());
    }
}
