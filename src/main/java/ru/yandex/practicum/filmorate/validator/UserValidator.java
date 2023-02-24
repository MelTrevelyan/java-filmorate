package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class UserValidator {

    public static void validateUser(User user) {
        validateEmail(user.getEmail());
        validateLogin(user.getLogin());
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        validateBirthday(user.getBirthday());
    }

    private static void validateEmail(String email) {
        if (email == null || !email.contains("@") || email.isBlank()) {
            log.warn("Ошибка валидации пользователя. Некорректный адрес электронной почты");
            throw new ValidationException();
        }
    }

    private static void validateLogin(String login) {
        if (login == null || login.isBlank() || login.contains(" ")) {
            log.warn("Ошибка валидации пользователя. Некорректный логин");
            throw new ValidationException();
        }
    }

    private static void validateBirthday(LocalDate birthday) {
        if (birthday == null || birthday.isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации пользователя. Дата рождения не может быть в будущем");
            throw new ValidationException();
        }
    }
}
