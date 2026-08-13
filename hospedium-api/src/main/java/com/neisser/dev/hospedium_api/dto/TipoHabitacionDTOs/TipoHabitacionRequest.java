package com.neisser.dev.hospedium_api.dto.TipoHabitacionDTOs;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TipoHabitacionRequest(
    @NotBlank(message = "El nombre del tipo de habitacion es requerido.")
    String nombre,

    @NotNull(message = "El precio es obligatorio.")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio debe ser mayor o igual a 0.")
    BigDecimal precioNoche
) {
}