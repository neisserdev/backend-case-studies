package com.neisser.dev.bpa.dto.ClienteDTOs;

import com.neisser.dev.bpa.model.ClasificacionEmpresa;

public record ClienteUpdateRequest(
        String nombre,

        String documento,

        ClasificacionEmpresa clasificacionEmpresa) {
}
