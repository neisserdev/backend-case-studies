package com.neisser.dev.biblioteca.dto.EjemplarDTOs;

import com.neisser.dev.biblioteca.model.EstadoEjemplar;

public record EjemplarResponse(
        Long id,
        EstadoEjemplar estadoEjemplar,
        Long libroId) {
}
