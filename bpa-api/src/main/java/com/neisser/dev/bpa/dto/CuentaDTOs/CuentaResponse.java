package com.neisser.dev.bpa.dto.CuentaDTOs;

import java.math.BigDecimal;

import com.neisser.dev.bpa.model.EstadoCuenta;

public record CuentaResponse(
        Long id,
        BigDecimal saldo,
        EstadoCuenta estadoCuenta,
        Long clienteId) {
}
