package com.neisser.dev.hospedium_api.dto.HuespedDTOs;

import jakarta.validation.constraints.NotBlank;

public record HuespedRequest(
        @NotBlank(message = "El nombre del huesped es obligatorio.") String nombre,

        @NotBlank(message = "El apellido del huesped es obligatorio.") String apellido,

        @NotBlank(message = "El pasaporte del huesped es obligatorio.") String pasaporte) {
}
