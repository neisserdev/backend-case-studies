package com.neisser.dev.bpa.dto.OperacionDTOs;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.neisser.dev.bpa.model.TipoOperacion;

public record OperacionResponse(
        Long id,
        TipoOperacion tipoOperacion,
        BigDecimal monto,
        BigDecimal saldoResultante,
        LocalDateTime fecha,
        Long cuentaId) {
}
