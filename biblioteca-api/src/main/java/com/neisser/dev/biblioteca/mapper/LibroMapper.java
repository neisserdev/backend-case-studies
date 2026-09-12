package com.neisser.dev.biblioteca.mapper;

import org.springframework.stereotype.Component;

import com.neisser.dev.biblioteca.dto.LibroDTOs.LibroRequest;
import com.neisser.dev.biblioteca.dto.LibroDTOs.LibroResponse;
import com.neisser.dev.biblioteca.dto.LibroDTOs.LibroUpdateRequest;
import com.neisser.dev.biblioteca.model.Libro;

@Component
public class LibroMapper {
    public Libro toEntity(LibroRequest request, String codigo) {
        return Libro.builder()
                .codigo(codigo)
                .titulo(request.titulo())
                .autor(request.autor())
                .genero(request.genero())
                .build();
    }

    public void actualizarEntidad(Libro libro, LibroUpdateRequest request) {
        if (request.titulo() != null) {
            libro.setTitulo(request.titulo());
        }
        if (request.autor() != null) {
            libro.setAutor(request.autor());
        }
    }

    public LibroResponse toResponse(Libro libro) {
        return new LibroResponse(
                libro.getId(),
                libro.getCodigo(),
                libro.getTitulo(),
                libro.getAutor(),
                libro.getGenero());
    }
}
