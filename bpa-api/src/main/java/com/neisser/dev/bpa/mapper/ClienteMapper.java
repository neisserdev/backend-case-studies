package com.neisser.dev.bpa.mapper;

import org.springframework.stereotype.Component;

import com.neisser.dev.bpa.dto.ClienteDTOs.ClienteRequest;
import com.neisser.dev.bpa.dto.ClienteDTOs.ClienteResponse;
import com.neisser.dev.bpa.dto.ClienteDTOs.ClienteUpdateRequest;
import com.neisser.dev.bpa.model.Cliente;

@Component
public class ClienteMapper {
    public Cliente toEntity(ClienteRequest request) {
        return Cliente.builder()
                .nombre(request.nombre())
                .documento(request.documento())
                .tipoCliente(request.tipoCliente())
                .clasificacionEmpresa(request.clasificacionEmpresa())
                .build();
    }

    public void actualizarEntidad(Cliente cliente, ClienteUpdateRequest request) {
        if (request.nombre() != null) {
            cliente.setNombre(request.nombre());
        }
        if (request.documento() != null) {
            cliente.setDocumento(request.documento());
        }
        if (request.clasificacionEmpresa() != null) {
            cliente.setClasificacionEmpresa(request.clasificacionEmpresa());
        }
    }

    public ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getDocumento(),
                cliente.getTipoCliente(),
                cliente.getClasificacionEmpresa());
    }
}

