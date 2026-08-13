package com.neisser.dev.hospedium_api.dto.HabitacionDTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HabitacionRequest(

                @NotBlank(message = "El numero de la habitacion es requerido.") String numero,

                @NotNull(message = "El piso de la habitacion es obligatorio.") @Min(0) Integer piso,

                @NotNull(message = "La habitacion debe tener un tipo valido.") Long tipoHabitacionId) {
}