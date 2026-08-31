package com.neisser.dev.bpa.dto.CuentaDTOs;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CuentaRequest(
        @NotNull(message = "El id del cliente es obligatorio.") @Positive(message = "El id del cliente debe ser mayor que 0.") Long clienteId,

        @NotNull(message = "El deposito inicial es obligatorio.")
        @Digits(integer = 17, fraction = 2, message = "El depósito inicial debe tener como máximo 2 decimales.") BigDecimal depositoInicial) {
}
