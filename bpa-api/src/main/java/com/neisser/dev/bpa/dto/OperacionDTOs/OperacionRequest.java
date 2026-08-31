package com.neisser.dev.bpa.dto.OperacionDTOs;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public record OperacionRequest(
        @NotNull(message = "El monto de la operacion es obligatorio.")
        @DecimalMin(value = "0.01", message = "El monto debe ser de al menos 0.01.")
        @Digits(integer = 17, fraction = 2, message = "El monto debe tener como máximo 2 decimales.") BigDecimal monto) {
}
