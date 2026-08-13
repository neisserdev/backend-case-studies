package com.neisser.dev.hospedium_api.dto.HuespedDTOs;

public record HuespedResponse(
        Long id,
        String nombre,
        String apellido,
        String pasaporte) {
}