package com.neisser.dev.biblioteca.dto.PrestamoDTOs;

import java.time.LocalDate;

import com.neisser.dev.biblioteca.model.TipoPrestamo;
import com.neisser.dev.biblioteca.validation.FechaDevolucionEsperadaValida;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@FechaDevolucionEsperadaValida
public record PrestamoRequest(
        @NotNull(message = "El id del ejemplar es obligatorio.") @Positive(message = "El id del ejemplar debe ser mayor que 0.") Long ejemplarId,

        @NotNull(message = "El id del usuario es obligatorio.") @Positive(message = "El id del usuario debe ser mayor que 0.") Long usuarioId,

        @NotNull(message = "El tipo de préstamo es obligatorio.") TipoPrestamo tipoPrestamo,

        LocalDate fechaDevolucionEsperada) {
}
