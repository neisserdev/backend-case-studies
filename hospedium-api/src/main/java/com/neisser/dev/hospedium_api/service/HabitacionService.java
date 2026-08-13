package com.neisser.dev.hospedium_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neisser.dev.hospedium_api.dto.HabitacionDTOs.HabitacionRequest;
import com.neisser.dev.hospedium_api.dto.HabitacionDTOs.HabitacionResponse;
import com.neisser.dev.hospedium_api.exceptions.ResourceConflictException;
import com.neisser.dev.hospedium_api.exceptions.ResourceNotFoundException;
import com.neisser.dev.hospedium_api.mapper.HabitacionMapper;
import com.neisser.dev.hospedium_api.model.Habitacion;
import com.neisser.dev.hospedium_api.model.TipoHabitacion;
import com.neisser.dev.hospedium_api.repository.AsignacionRepository;
import com.neisser.dev.hospedium_api.repository.HabitacionRepository;
import com.neisser.dev.hospedium_api.repository.TipoHabitacionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HabitacionService {
        private final HabitacionMapper habitacionMapper;
        private final HabitacionRepository habitacionRepository;
        private final TipoHabitacionRepository tipoHabitacionRepository;
        private final AsignacionRepository asignacionRepository;

        @Transactional
        public HabitacionResponse crear(HabitacionRequest request) {
                TipoHabitacion th = tipoHabitacionRepository.findById(request.tipoHabitacionId())
                                .orElseThrow(() -> new ResourceNotFoundException("tipoHabitacion",
                                                request.tipoHabitacionId()));

                return habitacionMapper.toResponse(
                                habitacionRepository.save(habitacionMapper.toEntity(request, th)));
        }

        @Transactional(readOnly = true)
        public List<HabitacionResponse> listarTodas() {
                return habitacionRepository.findAll()
                                .stream()
                                .map(habitacionMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public HabitacionResponse buscarPorId(Long id) {
                Habitacion habitacion = habitacionRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("habitacion", id));

                return habitacionMapper.toResponse(habitacion);
        }

        @Transactional
        public HabitacionResponse actualizar(Long id, HabitacionRequest request) {
                Habitacion habitacion = habitacionRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("habitacion", id));

                TipoHabitacion th = tipoHabitacionRepository.findById(request.tipoHabitacionId())
                                .orElseThrow(() -> new ResourceNotFoundException("tipoHabitacion",
                                                request.tipoHabitacionId()));

                habitacion.setNumero(request.numero());
                habitacion.setPiso(request.piso());
                habitacion.setTipoHabitacion(th);

                return habitacionMapper.toResponse(habitacion);
        }

        @Transactional
        public void eliminar(Long id) {

                if (!habitacionRepository.existsById(id)) {
                        throw new ResourceNotFoundException("Habitacion", id);
                }

                if (asignacionRepository.existsByHabitacionId(id)) {
                        throw new ResourceConflictException(
                                        "No se puede eliminar la habitación porque tiene asignaciones asociadas");
                }

                habitacionRepository.deleteById(id);
        }
}