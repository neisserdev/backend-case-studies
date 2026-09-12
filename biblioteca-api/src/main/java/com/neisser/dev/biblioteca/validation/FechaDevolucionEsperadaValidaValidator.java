package com.neisser.dev.biblioteca.validation;

import java.time.LocalDate;

import com.neisser.dev.biblioteca.dto.PrestamoDTOs.PrestamoRequest;
import com.neisser.dev.biblioteca.model.TipoPrestamo;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FechaDevolucionEsperadaValidaValidator
        implements ConstraintValidator<FechaDevolucionEsperadaValida, PrestamoRequest> {

    @Override
    public boolean isValid(PrestamoRequest request, ConstraintValidatorContext context) {
        if (request == null || request.tipoPrestamo() == null) {
            return true;
        }

        if (request.tipoPrestamo() == TipoPrestamo.VARIOS) {
            return request.fechaDevolucionEsperada() != null
                    && request.fechaDevolucionEsperada().isAfter(LocalDate.now());
        }

        return request.fechaDevolucionEsperada() == null;
    }
}
