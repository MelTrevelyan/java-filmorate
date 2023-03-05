package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {

    private final UserStorage userStorage = new InMemoryUserStorage();
    private final UserService userService = new UserService(userStorage);
    private UserController controller;
    private static Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @BeforeEach
    public void beforeEach() {
        controller = new UserController(userService);
    }

    @Test
    public void shouldCreateUser() {
        controller.create(User.builder()
                .login("Mango11")
                .name("Melissa")
                .email("nicemail@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build());

        assertEquals(1, controller.getUsers().size());
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
                .id(1L)
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
                .email("nicemail@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldNotPassLoginValidationWithBlanksInLogin() {
        User user = User.builder()
                .login(" Mango 11")
                .name("Melissa")
                .email("nicemail@mail.ru")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        assertThrows(ValidationException.class, () -> controller.create(user));
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
}
