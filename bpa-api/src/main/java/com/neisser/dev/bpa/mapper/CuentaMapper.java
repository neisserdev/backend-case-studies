package com.neisser.dev.bpa.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.neisser.dev.bpa.dto.CuentaDTOs.CuentaResponse;
import com.neisser.dev.bpa.model.Cliente;
import com.neisser.dev.bpa.model.Cuenta;

@Component
public class CuentaMapper {
    public Cuenta toEntity(Cliente cliente, BigDecimal depositoInicial) {
        return Cuenta.builder()
                .cliente(cliente)
                .saldo(depositoInicial)
                .build();
    }

    public CuentaResponse toResponse(Cuenta cuenta) {
        return new CuentaResponse(
                cuenta.getId(),
                cuenta.getSaldo(),
                cuenta.getEstadoCuenta(),
                cuenta.getCliente().getId());
    }
}
