package com.neisser.dev.hospedium_api.dto.ReservaDTOs;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReservaRequest(
        @NotNull(message = "La fecha de entrada es obligatoria.") LocalDate fechaEntrada,

        @NotNull(message = "La fecha de salida es obligatoria.") LocalDate fechaSalida,

        @NotNull(message = "El id del huesped es obligatorio.") @Positive(message = "El id del huesped debe ser mayor que 0.") Long huespedId,

        @NotNull(message = "El id del tipo de habitacion es obligatorio.") @Positive(message = "El id del tipo de habitacion debe ser mayor que 0.") Long tipoHabitacionId

) {
}