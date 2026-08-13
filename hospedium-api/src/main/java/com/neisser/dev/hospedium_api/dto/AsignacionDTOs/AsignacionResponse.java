package com.neisser.dev.hospedium_api.dto.AsignacionDTOs;

import java.time.LocalDate;

public record AsignacionResponse(
                Long id,
                Long reservaId,
                Long habitacionId,
                LocalDate fechaEntrada,
                LocalDate fechaSalida) {
}
