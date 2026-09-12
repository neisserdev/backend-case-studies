package com.neisser.dev.biblioteca.dto.LibroDTOs;

import com.neisser.dev.biblioteca.model.Genero;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LibroRequest(
        @NotBlank(message = "El título del libro es obligatorio.") String titulo,

        @NotBlank(message = "El autor del libro es obligatorio.") String autor,

        @NotNull(message = "El género del libro es obligatorio.") Genero genero) {
}
