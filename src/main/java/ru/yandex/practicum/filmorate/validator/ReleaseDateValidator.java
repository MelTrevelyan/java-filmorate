package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateValidation, LocalDate> {

    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext cxt) {
        return !releaseDate.isBefore(LocalDate.of(1895, 12, 28));
    }
}
