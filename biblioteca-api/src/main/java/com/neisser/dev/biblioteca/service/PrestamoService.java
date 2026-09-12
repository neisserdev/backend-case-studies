package com.neisser.dev.biblioteca.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neisser.dev.biblioteca.dto.PrestamoDTOs.PrestamoRequest;
import com.neisser.dev.biblioteca.dto.PrestamoDTOs.PrestamoResponse;
import com.neisser.dev.biblioteca.exceptions.BusinessRuleViolationException;
import com.neisser.dev.biblioteca.exceptions.ResourceNotFoundException;
import com.neisser.dev.biblioteca.mapper.PrestamoMapper;
import com.neisser.dev.biblioteca.model.Ejemplar;
import com.neisser.dev.biblioteca.model.EstadoEjemplar;
import com.neisser.dev.biblioteca.model.Prestamo;
import com.neisser.dev.biblioteca.model.TipoPrestamo;
import com.neisser.dev.biblioteca.model.TipoUsuario;
import com.neisser.dev.biblioteca.model.Usuario;
import com.neisser.dev.biblioteca.repository.EjemplarRepository;
import com.neisser.dev.biblioteca.repository.PrestamoRepository;
import com.neisser.dev.biblioteca.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    private static final long MAX_PRESTAMOS_INSCRITO = 15;
    private static final long MAX_PRESTAMOS_NO_INSCRITO = 5;
    private static final long MAX_PRESTAMOS_LIBRO_POR_MES = 10;

    private final PrestamoRepository prestamoRepository;
    private final EjemplarRepository ejemplarRepository;
    private final UsuarioRepository usuarioRepository;
    private final PrestamoMapper prestamoMapper;

    @Transactional
    public PrestamoResponse crear(PrestamoRequest request) {

        Ejemplar ejemplar = ejemplarRepository.findByIdForUpdate(request.ejemplarId())
                .orElseThrow(() -> new ResourceNotFoundException("ejemplar", request.ejemplarId()));

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("usuario", request.usuarioId()));

        if (ejemplar.getEstadoEjemplar() == EstadoEjemplar.PRESTADO) {
            throw new BusinessRuleViolationException(
                    "El ejemplar ya tiene un préstamo abierto");
        }

        long prestamosAbiertos = prestamoRepository
                .countByUsuarioIdAndFechaDevolucionRealIsNull(usuario.getId());
        long maximoPermitido = maximoPrestamosDe(usuario.getTipoUsuario());

        if (prestamosAbiertos >= maximoPermitido) {
            throw new BusinessRuleViolationException(
                    "El usuario alcanzó el máximo de %d préstamos abiertos".formatted(maximoPermitido));
        }

        Long libroId = ejemplar.getLibro().getId();
        LocalDateTime inicioMes = LocalDate.now(ZoneOffset.UTC).withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMes = inicioMes.plusMonths(1);

        long prestamosDelLibroEnElMes = prestamoRepository
                .contarPrestamosDelLibroEnElMes(libroId, inicioMes, finMes);

        if (prestamosDelLibroEnElMes >= MAX_PRESTAMOS_LIBRO_POR_MES) {
            throw new BusinessRuleViolationException(
                    "El libro alcanzó el máximo de %d préstamos este mes".formatted(MAX_PRESTAMOS_LIBRO_POR_MES));
        }

        ejemplar.setEstadoEjemplar(EstadoEjemplar.PRESTADO);

        Prestamo prestamo = prestamoRepository.save(
                prestamoMapper.toEntity(request, ejemplar, usuario));

        return prestamoMapper.toResponse(prestamo);
    }

    @Transactional(readOnly = true)
    public PrestamoResponse buscarPorId(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("prestamo", id));

        return prestamoMapper.toResponse(prestamo);
    }

    @Transactional
    public PrestamoResponse devolver(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("prestamo", id));

        if (prestamo.getFechaDevolucionReal() != null) {
            throw new BusinessRuleViolationException(
                    "El préstamo ya fue devuelto");
        }

        LocalDateTime ahora = LocalDateTime.now(ZoneOffset.UTC);

        if (prestamo.getTipoPrestamo() == TipoPrestamo.UNICO
                && !prestamo.getFechaPrestamo().toLocalDate().isEqual(ahora.toLocalDate())) {
            throw new BusinessRuleViolationException(
                    "Un préstamo UNICO debe devolverse el mismo día en que se registró");
        }

        prestamo.setFechaDevolucionReal(ahora);

        Ejemplar ejemplar = ejemplarRepository.findByIdForUpdate(prestamo.getEjemplar().getId())
                .orElseThrow(() -> new ResourceNotFoundException("ejemplar", prestamo.getEjemplar().getId()));
        ejemplar.setEstadoEjemplar(EstadoEjemplar.DISPONIBLE);

        return prestamoMapper.toResponse(prestamo);
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listarPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("usuario", usuarioId);
        }

        return prestamoRepository.findByUsuarioIdOrderByFechaPrestamoDesc(usuarioId)
                .stream()
                .map(prestamoMapper::toResponse)
                .toList();
    }

    private long maximoPrestamosDe(TipoUsuario tipoUsuario) {
        return switch (tipoUsuario) {
            case INSCRITO -> MAX_PRESTAMOS_INSCRITO;
            case NO_INSCRITO -> MAX_PRESTAMOS_NO_INSCRITO;
        };
    }
}
