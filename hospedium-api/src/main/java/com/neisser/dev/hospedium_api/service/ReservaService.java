package com.neisser.dev.hospedium_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neisser.dev.hospedium_api.dto.AsignacionDTOs.AsignacionRequest;
import com.neisser.dev.hospedium_api.dto.AsignacionDTOs.AsignacionResponse;
import com.neisser.dev.hospedium_api.dto.DisponibilidadRequest;
import com.neisser.dev.hospedium_api.dto.HabitacionDTOs.HabitacionResponse;
import com.neisser.dev.hospedium_api.dto.ReservaDTOs.ReservaRequest;
import com.neisser.dev.hospedium_api.dto.ReservaDTOs.ReservaResponse;
import com.neisser.dev.hospedium_api.exceptions.BusinessRuleViolationException;
import com.neisser.dev.hospedium_api.exceptions.ResourceNotFoundException;
import com.neisser.dev.hospedium_api.mapper.AsignacionMapper;
import com.neisser.dev.hospedium_api.mapper.HabitacionMapper;
import com.neisser.dev.hospedium_api.mapper.ReservaMapper;
import com.neisser.dev.hospedium_api.model.Asignacion;
import com.neisser.dev.hospedium_api.model.EstadoReserva;
import com.neisser.dev.hospedium_api.model.Habitacion;
import com.neisser.dev.hospedium_api.model.Huesped;
import com.neisser.dev.hospedium_api.model.Reserva;
import com.neisser.dev.hospedium_api.model.TipoHabitacion;
import com.neisser.dev.hospedium_api.repository.AsignacionRepository;
import com.neisser.dev.hospedium_api.repository.HabitacionRepository;
import com.neisser.dev.hospedium_api.repository.HuespedRepository;
import com.neisser.dev.hospedium_api.repository.ReservaRepository;
import com.neisser.dev.hospedium_api.repository.TipoHabitacionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final HuespedRepository huespedRepository;
    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final HabitacionRepository habitacionRepository;
    private final AsignacionRepository asignacionRepository;

    private final ReservaMapper reservaMapper;
    private final HabitacionMapper habitacionMapper;
    private final AsignacionMapper asignacionMapper;

    @Transactional
    public ReservaResponse crear(ReservaRequest request) {

        if (!request.fechaEntrada().isBefore(request.fechaSalida())) {
            throw new BusinessRuleViolationException(
                    "La fecha de entrada debe ser anterior a la fecha de salida");
        }

        Huesped huesped = huespedRepository
                .findById(request.huespedId())
                .orElseThrow(() -> new ResourceNotFoundException("Huésped", request.huespedId()));

        TipoHabitacion tipoHabitacion = tipoHabitacionRepository
                .findById(request.tipoHabitacionId())
                .orElseThrow(() -> new ResourceNotFoundException("TipoHabitacion", request.tipoHabitacionId()));

        Reserva reserva = reservaMapper.toEntity(
                request,
                huesped,
                tipoHabitacion);

        return reservaMapper.toResponse(
                reservaRepository.save(reserva));
    }

    @Transactional(readOnly = true)
    public ReservaResponse buscarPorId(Long id) {

        Reserva reserva = reservaRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));

        return reservaMapper.toResponse(reserva);
    }

    @Transactional
    public ReservaResponse cancelar(Long id) {

        Reserva reserva = reservaRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));

        if (reserva.getEstadoReserva() != EstadoReserva.PENDIENTE) {
            throw new BusinessRuleViolationException(
                    "Solo se pueden cancelar reservas pendientes");
        }

        reserva.setEstadoReserva(EstadoReserva.CANCELADA);

        return reservaMapper.toResponse(reserva);
    }

    @Transactional(readOnly = true)
    public List<HabitacionResponse> buscarDisponibles(
            DisponibilidadRequest request) {

        if (!request.fechaEntrada().isBefore(request.fechaSalida())) {
            throw new BusinessRuleViolationException(
                    "La fecha de entrada debe ser anterior a la fecha de salida");
        }

        return habitacionRepository.buscarDisponibles(
                request.tipoHabitacionId(),
                request.fechaEntrada(),
                request.fechaSalida())
                .stream()
                .map(habitacionMapper::toResponse)
                .toList();
    }

    @Transactional
    public AsignacionResponse asignar(
            Long reservaId,
            AsignacionRequest request) {

        Reserva reserva = reservaRepository
                .findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", reservaId));

        if (reserva.getEstadoReserva() != EstadoReserva.PENDIENTE) {
            throw new BusinessRuleViolationException(
                    "Solo se pueden asignar habitaciones a reservas pendientes");
        }

        Habitacion habitacion = habitacionRepository
                .findById(request.habitacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Habitación", request.habitacionId()));

        if (reserva.getTipoHabitacion() != null
                && !reserva.getTipoHabitacion().getId().equals(habitacion.getTipoHabitacion().getId())) {
            throw new BusinessRuleViolationException(
                    "La habitación asignada no coincide con el tipo de habitación reservado");
        }

        if (asignacionRepository.existeChoque(
                habitacion.getId(),
                reserva.getFechaEntrada(),
                reserva.getFechaSalida())) {
            throw new BusinessRuleViolationException(
                    "La habitación no está disponible para esas fechas");
        }

        Asignacion asignacion = asignacionMapper.toEntity(
                request,
                reserva,
                habitacion);

        Asignacion guardada = asignacionRepository.save(asignacion);

        reserva.setEstadoReserva(EstadoReserva.ASIGNADA);

        return asignacionMapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> historial(Long huespedId) {

        if (!huespedRepository.existsById(huespedId)) {
            throw new ResourceNotFoundException("Huésped", huespedId);
        }

        return reservaRepository
                .findByHuespedIdOrderByFechaEntradaDesc(huespedId)
                .stream()
                .map(reservaMapper::toResponse)
                .toList();
    }
}
