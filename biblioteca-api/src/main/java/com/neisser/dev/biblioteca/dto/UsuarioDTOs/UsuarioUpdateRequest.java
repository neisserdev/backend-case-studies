package com.neisser.dev.biblioteca.dto.UsuarioDTOs;

import jakarta.validation.constraints.Pattern;

public record UsuarioUpdateRequest(
        @Pattern(regexp = ".*\\S.*", message = "El nombre del usuario no puede estar vacío.") String nombre,

        @Pattern(regexp = ".*\\S.*", message = "El documento del usuario no puede estar vacío.") String documento) {
}
