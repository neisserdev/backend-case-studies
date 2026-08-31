package com.neisser.dev.bpa.dto.ClienteDTOs;

import com.neisser.dev.bpa.model.ClasificacionEmpresa;

import jakarta.validation.constraints.Pattern;

public record ClienteUpdateRequest(
        @Pattern(regexp = ".*\\S.*", message = "El nombre del cliente no puede estar vacío.") String nombre,

        @Pattern(regexp = ".*\\S.*", message = "El documento del cliente no puede estar vacío.") String documento,

        ClasificacionEmpresa clasificacionEmpresa) {
}
