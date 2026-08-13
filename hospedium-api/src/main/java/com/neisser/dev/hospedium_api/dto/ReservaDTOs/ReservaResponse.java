package com.neisser.dev.hospedium_api.dto.ReservaDTOs;

import java.time.LocalDate;

import com.neisser.dev.hospedium_api.model.EstadoReserva;

public record ReservaResponse(
        Long id,
        LocalDate fechaEntrada,
        LocalDate fechaSalida,
        EstadoReserva estadoReserva,
        Long huespedId,
        Long tipoHabitacionId) {
}