package com.neisser.dev.biblioteca.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.neisser.dev.biblioteca.dto.PrestamoDTOs.PrestamoRequest;
import com.neisser.dev.biblioteca.dto.UsuarioDTOs.UsuarioUpdateRequest;
import com.neisser.dev.biblioteca.model.TipoPrestamo;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class RequestValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void configurarValidador() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void cerrarValidador() {
        factory.close();
    }

    @Test
    void prestamoVariosRequiereFechaDevolucionEsperadaFutura() {
        assertFalse(validator.validate(
                new PrestamoRequest(1L, 1L, TipoPrestamo.VARIOS, null)).isEmpty());
        assertFalse(validator.validate(
                new PrestamoRequest(1L, 1L, TipoPrestamo.VARIOS, LocalDate.now().minusDays(1))).isEmpty());
        assertTrue(validator.validate(
                new PrestamoRequest(1L, 1L, TipoPrestamo.VARIOS, LocalDate.now().plusDays(7))).isEmpty());
    }

    @Test
    void prestamoUnicoNoAdmiteFechaDevolucionEsperada() {
        assertFalse(validator.validate(
                new PrestamoRequest(1L, 1L, TipoPrestamo.UNICO, LocalDate.now().plusDays(7))).isEmpty());
        assertTrue(validator.validate(
                new PrestamoRequest(1L, 1L, TipoPrestamo.UNICO, null)).isEmpty());
    }

    @Test
    void actualizacionParcialRechazaTextosVaciosPeroAdmiteCamposOmitidos() {
        assertFalse(validator.validate(new UsuarioUpdateRequest("", "   ")).isEmpty());
        assertTrue(validator.validate(new UsuarioUpdateRequest("Nombre válido", null)).isEmpty());
    }
}
