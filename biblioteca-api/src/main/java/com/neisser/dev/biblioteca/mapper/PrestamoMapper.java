package com.neisser.dev.biblioteca.mapper;

import org.springframework.stereotype.Component;

import com.neisser.dev.biblioteca.dto.PrestamoDTOs.PrestamoRequest;
import com.neisser.dev.biblioteca.dto.PrestamoDTOs.PrestamoResponse;
import com.neisser.dev.biblioteca.model.Ejemplar;
import com.neisser.dev.biblioteca.model.Prestamo;
import com.neisser.dev.biblioteca.model.Usuario;

@Component
public class PrestamoMapper {
    public Prestamo toEntity(PrestamoRequest request, Ejemplar ejemplar, Usuario usuario) {
        return Prestamo.builder()
                .tipoPrestamo(request.tipoPrestamo())
                .fechaDevolucionEsperada(request.fechaDevolucionEsperada())
                .ejemplar(ejemplar)
                .usuario(usuario)
                .build();
    }

    public PrestamoResponse toResponse(Prestamo prestamo) {
        return new PrestamoResponse(
                prestamo.getId(),
                prestamo.getTipoPrestamo(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucionEsperada(),
                prestamo.getFechaDevolucionReal(),
                prestamo.getEjemplar().getId(),
                prestamo.getUsuario().getId());
    }
}
