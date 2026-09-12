package com.neisser.dev.biblioteca.mapper;

import org.springframework.stereotype.Component;

import com.neisser.dev.biblioteca.dto.EjemplarDTOs.EjemplarResponse;
import com.neisser.dev.biblioteca.model.Ejemplar;
import com.neisser.dev.biblioteca.model.Libro;

@Component
public class EjemplarMapper {
    public Ejemplar toEntity(Libro libro) {
        return Ejemplar.builder()
                .libro(libro)
                .build();
    }

    public EjemplarResponse toResponse(Ejemplar ejemplar) {
        return new EjemplarResponse(
                ejemplar.getId(),
                ejemplar.getEstadoEjemplar(),
                ejemplar.getLibro().getId());
    }
}
