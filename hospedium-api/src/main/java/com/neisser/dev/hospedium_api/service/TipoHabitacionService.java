package com.neisser.dev.hospedium_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neisser.dev.hospedium_api.dto.TipoHabitacionDTOs.TipoHabitacionRequest;
import com.neisser.dev.hospedium_api.dto.TipoHabitacionDTOs.TipoHabitacionResponse;
import com.neisser.dev.hospedium_api.exceptions.ResourceNotFoundException;
import com.neisser.dev.hospedium_api.mapper.TipoHabitacionMapper;
import com.neisser.dev.hospedium_api.model.TipoHabitacion;
import com.neisser.dev.hospedium_api.repository.TipoHabitacionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoHabitacionService {
    private final TipoHabitacionMapper tipoHabitacionMapper;
    private final TipoHabitacionRepository tipoHabitacionRepository;

    @Transactional
    public TipoHabitacionResponse crear(TipoHabitacionRequest request) {
        return tipoHabitacionMapper.toResponse(
                tipoHabitacionRepository.save(tipoHabitacionMapper.toEntity(request)));
    }

    @Transactional(readOnly = true)
    public List<TipoHabitacionResponse> mostrarTodos() {
        return tipoHabitacionRepository.findAll()
                .stream()
                .map(tipoHabitacionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TipoHabitacionResponse buscarPorId(Long id) {
        TipoHabitacion th = tipoHabitacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("tipoHabitacion", id));

        return tipoHabitacionMapper.toResponse(th);
    }

    @Transactional
    public TipoHabitacionResponse actualizar(TipoHabitacionRequest request, Long id) {
        TipoHabitacion th = tipoHabitacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("tipoHabitacion", id));

        th.setNombre(request.nombre());
        th.setPrecioNoche(request.precioNoche());

        return tipoHabitacionMapper.toResponse(th);
    }

    @Transactional
    public void eliminar(Long id) {
        TipoHabitacion th = tipoHabitacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("tipoHabitacion", id));

        tipoHabitacionRepository.delete(th);
    }
}
