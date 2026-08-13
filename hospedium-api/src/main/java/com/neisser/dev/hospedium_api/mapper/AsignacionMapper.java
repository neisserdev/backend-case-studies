package com.neisser.dev.hospedium_api.mapper;

import org.springframework.stereotype.Component;

import com.neisser.dev.hospedium_api.dto.AsignacionDTOs.AsignacionRequest;
import com.neisser.dev.hospedium_api.dto.AsignacionDTOs.AsignacionResponse;
import com.neisser.dev.hospedium_api.model.Asignacion;
import com.neisser.dev.hospedium_api.model.Habitacion;
import com.neisser.dev.hospedium_api.model.Reserva;

@Component
public class AsignacionMapper {

    public Asignacion toEntity(
            AsignacionRequest request,
            Reserva reserva,
            Habitacion habitacion) {
        return Asignacion.builder()
                .reserva(reserva)
                .habitacion(habitacion)
                .build();
    }

    public AsignacionResponse toResponse(Asignacion asignacion) {
        return new AsignacionResponse(
                asignacion.getId(),
                asignacion.getReserva().getId(),
                asignacion.getHabitacion().getId(),
                asignacion.getReserva().getFechaEntrada(),
                asignacion.getReserva().getFechaSalida());
    }
}