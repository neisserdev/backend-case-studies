package com.neisser.dev.biblioteca.dto.LibroDTOs;

import jakarta.validation.constraints.Pattern;

public record LibroUpdateRequest(
        @Pattern(regexp = ".*\\S.*", message = "El título del libro no puede estar vacío.") String titulo,

        @Pattern(regexp = ".*\\S.*", message = "El autor del libro no puede estar vacío.") String autor) {
}
