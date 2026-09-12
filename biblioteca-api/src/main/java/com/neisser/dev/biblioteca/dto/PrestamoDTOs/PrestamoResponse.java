package com.neisser.dev.biblioteca.dto.PrestamoDTOs;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.neisser.dev.biblioteca.model.TipoPrestamo;

public record PrestamoResponse(
        Long id,
        TipoPrestamo tipoPrestamo,
        LocalDateTime fechaPrestamo,
        LocalDate fechaDevolucionEsperada,
        LocalDateTime fechaDevolucionReal,
        Long ejemplarId,
        Long usuarioId) {
}
