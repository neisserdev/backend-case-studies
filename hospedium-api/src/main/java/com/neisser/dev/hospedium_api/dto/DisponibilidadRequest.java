package com.neisser.dev.hospedium_api.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DisponibilidadRequest(
        @NotNull(message = "El id del tipo de habitacion es obligatorio.") @Positive(message = "El id del tipo de habitacion debe ser mayor que 0.") Long tipoHabitacionId,

        @NotNull(message = "La fecha de entrada es obligatoria.") LocalDate fechaEntrada,

        @NotNull(message = "La fecha de salida es obligatoria.") LocalDate fechaSalida) {
}
