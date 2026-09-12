package com.neisser.dev.biblioteca.dto.LibroDTOs;

import com.neisser.dev.biblioteca.model.Genero;

public record LibroResponse(
        Long id,
        String codigo,
        String titulo,
        String autor,
        Genero genero) {
}
