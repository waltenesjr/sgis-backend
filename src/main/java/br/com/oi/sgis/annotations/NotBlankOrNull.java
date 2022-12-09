package br.com.oi.sgis.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target( {ElementType.FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = CustomTrimSizeValidator.class)
public @interface NotBlankOrNull {
    String message() default "{javax.validation.constraints.NotBlankOrNull.message}";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default {};
}
