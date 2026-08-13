package com.neisser.dev.hospedium_api.dto.HabitacionDTOs;

import com.neisser.dev.hospedium_api.model.TipoHabitacion;

public record HabitacionResponse(
                Long id,
                String numero,
                Integer piso,
                TipoHabitacion tipoHabitacion) {
}