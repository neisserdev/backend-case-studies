package com.neisser.dev.bpa.mapper;

import org.springframework.stereotype.Component;

import com.neisser.dev.bpa.dto.OperacionDTOs.OperacionResponse;
import com.neisser.dev.bpa.model.Operacion;

@Component
public class OperacionMapper {
    public OperacionResponse toResponse(Operacion operacion) {
        return new OperacionResponse(
                operacion.getId(),
                operacion.getTipoOperacion(),
                operacion.getMonto(),
                operacion.getSaldoResultante(),
                operacion.getFecha(),
                operacion.getCuenta().getId());
    }
}

