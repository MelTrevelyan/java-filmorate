package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.FilmService;

import ru.yandex.practicum.filmorate.model.User;

import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {

    private final UserService userService;
    private final FilmService filmService;

    private final EventService eventService;
    private static Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void shouldCreateUser() {
        User user = User.builder()
                .login("Mango11")
                .name("Melissa")
                .email("voiceemail@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(user);

        assertEquals("voiceemail@mail.ru", userService.findUserById(user.getId()).getEmail());
    }

    @Test
    public void shouldUpdateUser() {
        User user = User.builder()
                .login("Mango11")
                .name("Melissa")
                .email("nicemail@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(user);

        User userUpdate = User.builder()
                .id(user.getId())
                .login("Mango22")
                .name("Anna")
                .email("palmtree@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        userService.update(userUpdate);

        assertEquals(userUpdate, userService.findUserById(user.getId()));
    }

    @Test
    public void shouldCreateUserWithEmptyName() {
        User user = User.builder()
                .login("Mangosteen11")
                .email("nicestmail@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        userService.create(user);

        assertEquals("Mangosteen11", user.getName());
    }

    @Test
    void shouldNotPassEmailValidation() {
        User user = User.builder()
                .login("Mango11")
                .name("Melissa")
                .email("nicemailmail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldNotPassLoginValidationWithEmptyLogin() {
        User user = User.builder()
                .login("")
                .name("Melissa")
                .email("Jolly@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(2, violations.size());
    }

    @Test
    public void shouldNotPassLoginValidationWithBlanksInLogin() {
        User user = User.builder()
                .login(" Mango 11")
                .name("Melissa")
                .email("nicemail@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldNotPassBirthdayValidation() {
        User user = User.builder()
                .login("Mango11")
                .name("Melissa")
                .email("nicemail@mail.ru")
                .birthday(LocalDate.of(3000, 8, 15))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldAddFriend() {
        User user = User.builder()
                .login("Tango11")
                .name("Melissa")
                .email("rhino@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(user);
        User friend = User.builder()
                .login("Sango11")
                .name("Melissa")
                .email("mouse@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(friend);

        userService.addFriend(user.getId(), friend.getId());

        assertEquals(List.of(friend), userService.getAllFriends(user.getId()));
    }

    @Test
    public void shouldDeleteFriend() {
        User user = User.builder()
                .login("Wind")
                .name("Melissa")
                .email("cheetah@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(user);
        User friend = User.builder()
                .login("Iris")
                .name("Melissa")
                .email("cat@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(friend);

        userService.addFriend(user.getId(), friend.getId());
        userService.removeFromFriends(user.getId(), friend.getId());

        assertEquals(Collections.emptyList(), List.copyOf(user.getFriends()));
    }

    @Test
    public void shouldFindMutualFriend() {
        User user = User.builder()
                .login("Windy")
                .name("Melissa")
                .email("cheetahcat@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(user);
        User friend = User.builder()
                .login("Iris")
                .name("Melissa")
                .email("cattycat@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(friend);
        User mutualFriend = User.builder()
                .login("Rose")
                .name("Melissa")
                .email("rose@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(mutualFriend);

        userService.addFriend(user.getId(), mutualFriend.getId());
        userService.addFriend(friend.getId(), mutualFriend.getId());

        List<User> mutual = userService.getMutualFriends(user.getId(), friend.getId());

        assertEquals(List.of(mutualFriend), mutual);
    }

    @Test
    public void shouldReturnAllFriends() {
        User user = User.builder()
                .login("Windy")
                .name("Melissa")
                .email("snowy@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(user);
        User friend = User.builder()
                .login("Iris")
                .name("Melissa")
                .email("stormy@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        userService.create(friend);
        userService.addFriend(user.getId(), friend.getId());

        assertEquals(1, userService.getAllFriends(user.getId()).size());
    }

    @Test
    public void shouldDeleteUser() {
        User user = User.builder()
                .login("Hazel")
                .name("Melissa")
                .email("hazelnut@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        userService.create(user);
        userService.deleteUser(user.getId());

        assertFalse(userService.getUsers().contains(user));
    }

    @Test
    public void shouldCreateUserEvents() {
        User user = User.builder()
                .login("Iri")
                .name("Mel")
                .email("Meliry@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(user);
        User user2 = User.builder()
                .login("Markich")
                .name("Mark")
                .email("mark@mail.ru")
                .birthday(LocalDate.of(2002, 8, 15))
                .build();
        userService.create(user2);
        Film film = Film.builder()
                .name("Форрест Гамп")
                .description("Жизнь как коробка конфет")
                .duration(192)
                .releaseDate(LocalDate.of(1981, 12, 6))
                .mpa(new Mpa(1, "PG"))
                .build();
        filmService.create(film);
        Event event = new Event(user.getId(), EventType.LIKE, EventOperation.ADD, film.getId());
        Event event1 = new Event(user.getId(), EventType.FRIEND, EventOperation.ADD, user2.getId());
        eventService.addEvent(event);
        eventService.addEvent(event1);
        assertEquals(2, eventService.findUserEvent(user.getId()).size());
        assertEquals(eventService.findUserEvent(user.getId()).get(0).getUserId(),user.getId());
        assertEquals(eventService.findUserEvent(user.getId()).get(0).getEventType(),EventType.LIKE);
        assertEquals(eventService.findUserEvent(user.getId()).get(1).getEventOperation(),EventOperation.ADD);
    }
}
