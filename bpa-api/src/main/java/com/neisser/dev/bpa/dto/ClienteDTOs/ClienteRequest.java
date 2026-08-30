package com.neisser.dev.bpa.dto.ClienteDTOs;

import com.neisser.dev.bpa.model.ClasificacionEmpresa;
import com.neisser.dev.bpa.model.TipoCliente;
import com.neisser.dev.bpa.validation.ClasificacionEmpresaRequerida;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ClasificacionEmpresaRequerida
public record ClienteRequest(
        @NotBlank(message = "El nombre del cliente es obligatorio.") String nombre,

        @NotBlank(message = "El documento del cliente es obligatorio.") String documento,

        @NotNull(message = "El tipo de cliente es obligatorio.") TipoCliente tipoCliente,

        ClasificacionEmpresa clasificacionEmpresa) {
}

