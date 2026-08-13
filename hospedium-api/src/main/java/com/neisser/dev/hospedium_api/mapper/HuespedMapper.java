package com.neisser.dev.hospedium_api.mapper;

import org.springframework.stereotype.Component;

import com.neisser.dev.hospedium_api.dto.HuespedDTOs.HuespedRequest;
import com.neisser.dev.hospedium_api.dto.HuespedDTOs.HuespedResponse;
import com.neisser.dev.hospedium_api.model.Huesped;

@Component
public class HuespedMapper {
    public Huesped toEntity(HuespedRequest request) {
        return Huesped.builder()
                .nombre(request.nombre())
                .apellidos(request.apellido())
                .pasaporte(request.pasaporte())
                .build();
    }

    public HuespedResponse toResponse(Huesped huesped) {
        return new HuespedResponse(
                huesped.getId(),
                huesped.getNombre(),
                huesped.getApellidos(),
                huesped.getPasaporte());
    }
}
