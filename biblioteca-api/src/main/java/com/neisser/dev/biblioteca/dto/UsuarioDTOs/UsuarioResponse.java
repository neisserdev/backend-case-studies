package com.neisser.dev.biblioteca.dto.UsuarioDTOs;

import com.neisser.dev.biblioteca.model.TipoUsuario;

public record UsuarioResponse(
        Long id,
        String nombre,
        String documento,
        TipoUsuario tipoUsuario) {
}
