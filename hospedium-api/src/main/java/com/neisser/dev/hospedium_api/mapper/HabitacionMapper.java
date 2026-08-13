package com.neisser.dev.hospedium_api.mapper;

import org.springframework.stereotype.Component;

import com.neisser.dev.hospedium_api.dto.HabitacionDTOs.HabitacionRequest;
import com.neisser.dev.hospedium_api.dto.HabitacionDTOs.HabitacionResponse;
import com.neisser.dev.hospedium_api.model.Habitacion;
import com.neisser.dev.hospedium_api.model.TipoHabitacion;

@Component
public class HabitacionMapper {
    public Habitacion toEntity(HabitacionRequest request, TipoHabitacion th) {
        return Habitacion.builder()
                .numero(request.numero())
                .piso(request.piso())
                .tipoHabitacion(th)
                .build();
    }

    public HabitacionResponse toResponse(Habitacion habitacion) {
        return new HabitacionResponse(
                habitacion.getId(),
                habitacion.getNumero(),
                habitacion.getPiso(),
                habitacion.getTipoHabitacion());
    }
}
