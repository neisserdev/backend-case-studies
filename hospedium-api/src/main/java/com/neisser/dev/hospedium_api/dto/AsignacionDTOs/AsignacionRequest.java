package com.neisser.dev.hospedium_api.dto.AsignacionDTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AsignacionRequest(
        @NotNull(message = "El id de la habitacion es obligatorio.") @Positive(message = "El id de la habitacion debe ser mayor que 0.") Long habitacionId) {
}