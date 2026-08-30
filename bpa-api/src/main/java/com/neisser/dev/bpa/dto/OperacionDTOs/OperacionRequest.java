package com.neisser.dev.bpa.dto.OperacionDTOs;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record OperacionRequest(
        @NotNull(message = "El monto de la operacion es obligatorio.") @DecimalMin(value = "0.01", message = "El monto debe ser mayor que 0.") BigDecimal monto) {
}
