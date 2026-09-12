package com.neisser.dev.biblioteca.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neisser.dev.biblioteca.dto.EjemplarDTOs.EjemplarResponse;
import com.neisser.dev.biblioteca.exceptions.BusinessRuleViolationException;
import com.neisser.dev.biblioteca.exceptions.ResourceNotFoundException;
import com.neisser.dev.biblioteca.mapper.EjemplarMapper;
import com.neisser.dev.biblioteca.model.Ejemplar;
import com.neisser.dev.biblioteca.model.Libro;
import com.neisser.dev.biblioteca.repository.EjemplarRepository;
import com.neisser.dev.biblioteca.repository.LibroRepository;
import com.neisser.dev.biblioteca.repository.PrestamoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EjemplarService {
    private final EjemplarRepository ejemplarRepository;
    private final LibroRepository libroRepository;
    private final PrestamoRepository prestamoRepository;
    private final EjemplarMapper ejemplarMapper;

    @Transactional
    public EjemplarResponse crear(Long libroId) {
        Libro libro = libroRepository.findById(libroId)
                .orElseThrow(() -> new ResourceNotFoundException("libro", libroId));

        return ejemplarMapper.toResponse(
                ejemplarRepository.save(ejemplarMapper.toEntity(libro)));
    }

    @Transactional(readOnly = true)
    public List<EjemplarResponse> listarPorLibro(Long libroId) {
        if (!libroRepository.existsById(libroId)) {
            throw new ResourceNotFoundException("libro", libroId);
        }

        return ejemplarRepository.findByLibroId(libroId)
                .stream()
                .map(ejemplarMapper::toResponse)
                .toList();
    }

    @Transactional
    public void eliminar(Long id) {
        Ejemplar ejemplar = ejemplarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ejemplar", id));

        if (prestamoRepository.existsByEjemplarIdAndFechaDevolucionRealIsNull(ejemplar.getId())) {
            throw new BusinessRuleViolationException(
                    "No se puede eliminar un ejemplar con un préstamo abierto");
        }

        ejemplarRepository.delete(ejemplar);
    }
}
