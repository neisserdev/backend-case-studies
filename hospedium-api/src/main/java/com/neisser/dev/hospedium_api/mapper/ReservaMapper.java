package com.neisser.dev.hospedium_api.mapper;

import org.springframework.stereotype.Component;

import com.neisser.dev.hospedium_api.dto.ReservaDTOs.ReservaRequest;
import com.neisser.dev.hospedium_api.dto.ReservaDTOs.ReservaResponse;
import com.neisser.dev.hospedium_api.model.TipoHabitacion;
import com.neisser.dev.hospedium_api.model.Huesped;
import com.neisser.dev.hospedium_api.model.Reserva;

@Component
public class ReservaMapper {
    public Reserva toEntity(ReservaRequest request, Huesped huesped, TipoHabitacion tipoHabitacion) {
        return Reserva.builder()
                .fechaEntrada(request.fechaEntrada())
                .fechaSalida(request.fechaSalida())
                .huesped(huesped)
                .tipoHabitacion(tipoHabitacion)
                .build();
    }

    public ReservaResponse toResponse(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getFechaEntrada(),
                reserva.getFechaSalida(),
                reserva.getEstadoReserva(),
                reserva.getHuesped().getId(),
                reserva.getTipoHabitacion().getId());
    }

}
