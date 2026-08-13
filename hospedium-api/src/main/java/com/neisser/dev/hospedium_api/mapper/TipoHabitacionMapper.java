package com.neisser.dev.hospedium_api.mapper;

import org.springframework.stereotype.Component;

import com.neisser.dev.hospedium_api.dto.TipoHabitacionDTOs.TipoHabitacionRequest;
import com.neisser.dev.hospedium_api.dto.TipoHabitacionDTOs.TipoHabitacionResponse;
import com.neisser.dev.hospedium_api.model.TipoHabitacion;

@Component
public class TipoHabitacionMapper {
    public TipoHabitacion toEntity(TipoHabitacionRequest request){
        return TipoHabitacion.builder()
        .nombre(request.nombre())
        .precioNoche(request.precioNoche())
        .build();
    }

    public TipoHabitacionResponse toResponse(TipoHabitacion tipoHabitacion){
        return new TipoHabitacionResponse(
            tipoHabitacion.getId(),
            tipoHabitacion.getNombre(),
            tipoHabitacion.getPrecioNoche()
        );
        }
    }