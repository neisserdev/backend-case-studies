package com.neisser.dev.biblioteca.dto.UsuarioDTOs;

import com.neisser.dev.biblioteca.model.TipoUsuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequest(
        @NotBlank(message = "El nombre del usuario es obligatorio.") String nombre,

        @NotBlank(message = "El documento del usuario es obligatorio.") String documento,

        @NotNull(message = "El tipo de usuario es obligatorio.") TipoUsuario tipoUsuario) {
}
