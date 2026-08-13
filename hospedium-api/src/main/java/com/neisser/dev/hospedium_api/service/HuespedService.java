package com.neisser.dev.hospedium_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neisser.dev.hospedium_api.dto.HuespedDTOs.HuespedRequest;
import com.neisser.dev.hospedium_api.dto.HuespedDTOs.HuespedResponse;
import com.neisser.dev.hospedium_api.exceptions.ResourceConflictException;
import com.neisser.dev.hospedium_api.exceptions.ResourceNotFoundException;
import com.neisser.dev.hospedium_api.mapper.HuespedMapper;
import com.neisser.dev.hospedium_api.model.Huesped;
import com.neisser.dev.hospedium_api.repository.HuespedRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HuespedService {
    private final HuespedRepository huespedRepository;
    private final HuespedMapper huespedMapper;

    @Transactional
    public HuespedResponse crear(HuespedRequest request) {
        if (huespedRepository.existsByPasaporte(request.pasaporte())) {
            throw new ResourceConflictException("Ya existe un huesped con ese pasaporte");
        }

        return huespedMapper.toResponse(
                huespedRepository.save(huespedMapper.toEntity(request)));
    }

    @Transactional(readOnly = true)
    public HuespedResponse buscarPorId(Long id) {
        Huesped huesped = huespedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("huesped", id));

        return huespedMapper.toResponse(huesped);
    }
}