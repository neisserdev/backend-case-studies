package com.neisser.dev.bpa.dto.ClienteDTOs;

import com.neisser.dev.bpa.model.ClasificacionEmpresa;
import com.neisser.dev.bpa.model.TipoCliente;

public record ClienteResponse(
        Long id,
        String nombre,
        String documento,
        TipoCliente tipoCliente,
        ClasificacionEmpresa clasificacionEmpresa) {
}
