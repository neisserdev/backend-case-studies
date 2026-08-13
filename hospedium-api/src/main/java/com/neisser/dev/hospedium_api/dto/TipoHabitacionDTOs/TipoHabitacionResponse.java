package com.neisser.dev.hospedium_api.dto.TipoHabitacionDTOs;

import java.math.BigDecimal;

public record TipoHabitacionResponse(
    Long id,
    String nombre,
    BigDecimal precioNoche
) {
}