package com.neisser.dev.biblioteca.service;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neisser.dev.biblioteca.dto.LibroDTOs.LibroRequest;
import com.neisser.dev.biblioteca.dto.LibroDTOs.LibroResponse;
import com.neisser.dev.biblioteca.dto.LibroDTOs.LibroUpdateRequest;
import com.neisser.dev.biblioteca.exceptions.BusinessRuleViolationException;
import com.neisser.dev.biblioteca.exceptions.ResourceNotFoundException;
import com.neisser.dev.biblioteca.mapper.LibroMapper;
import com.neisser.dev.biblioteca.model.Genero;
import com.neisser.dev.biblioteca.model.Libro;
import com.neisser.dev.biblioteca.repository.LibroRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LibroService {

    private static final int MAX_INTENTOS_CODIGO = 20;

    private final LibroRepository libroRepository;
    private final LibroMapper libroMapper;

    private final SecureRandom random = new SecureRandom();

    @Transactional
    public LibroResponse crear(LibroRequest request) {
        String codigo = generarCodigoUnico(request.genero());

        return libroMapper.toResponse(
                libroRepository.save(libroMapper.toEntity(request, codigo)));
    }

    @Transactional(readOnly = true)
    public LibroResponse buscarPorId(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("libro", id));

        return libroMapper.toResponse(libro);
    }

    @Transactional(readOnly = true)
    public List<LibroResponse> listar() {
        return libroRepository.findAll()
                .stream()
                .map(libroMapper::toResponse)
                .toList();
    }

    @Transactional
    public LibroResponse actualizar(Long id, LibroUpdateRequest request) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("libro", id));

        libroMapper.actualizarEntidad(libro, request);

        return libroMapper.toResponse(libro);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!libroRepository.existsById(id)) {
            throw new ResourceNotFoundException("libro", id);
        }

        libroRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<LibroResponse> disponiblesPorGenero(Genero genero) {
        return libroRepository.buscarConEjemplarDisponiblePorGenero(genero)
                .stream()
                .map(libroMapper::toResponse)
                .toList();
    }

    private String generarCodigoUnico(Genero genero) {
        String prefijo = genero.name().substring(0, 2).toUpperCase();

        for (int intento = 0; intento < MAX_INTENTOS_CODIGO; intento++) {
            String candidato = prefijo + "%04d".formatted(random.nextInt(10_000));
            if (!libroRepository.existsByCodigo(candidato)) {
                return candidato;
            }
        }

        throw new BusinessRuleViolationException(
                "No fue posible generar un código único para el género " + genero);
    }
}
