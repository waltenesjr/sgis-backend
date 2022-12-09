package br.com.oi.sgis.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomTrimSizeValidator implements ConstraintValidator<NotBlankOrNull, String> {


    @Override
    public void initialize(NotBlankOrNull constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || value.trim().length() > 0;
    }
}
