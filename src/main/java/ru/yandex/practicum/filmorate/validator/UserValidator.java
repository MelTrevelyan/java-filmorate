package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
public class UserValidator {

    public static void validateUser(User user) {
        validateLogin(user.getLogin());
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    private static void validateLogin(String login) {
        if (login.contains(" ")) {
            log.warn("Ошибка валидации пользователя. Логин не может содержать пробелы");
            throw new ValidationException();
        }
    }
}
