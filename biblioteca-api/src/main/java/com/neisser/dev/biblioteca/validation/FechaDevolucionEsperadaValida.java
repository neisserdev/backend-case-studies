package com.neisser.dev.biblioteca.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = FechaDevolucionEsperadaValidaValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FechaDevolucionEsperadaValida {

    String message() default "Un préstamo VARIOS requiere fechaDevolucionEsperada posterior a la fecha del préstamo, y un préstamo UNICO no la admite.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
