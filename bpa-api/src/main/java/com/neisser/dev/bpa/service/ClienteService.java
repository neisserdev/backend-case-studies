package com.neisser.dev.bpa.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neisser.dev.bpa.dto.ClienteDTOs.ClienteRequest;
import com.neisser.dev.bpa.dto.ClienteDTOs.ClienteResponse;
import com.neisser.dev.bpa.dto.ClienteDTOs.ClienteUpdateRequest;
import com.neisser.dev.bpa.exceptions.BusinessRuleViolationException;
import com.neisser.dev.bpa.exceptions.ResourceConflictException;
import com.neisser.dev.bpa.exceptions.ResourceNotFoundException;
import com.neisser.dev.bpa.mapper.ClienteMapper;
import com.neisser.dev.bpa.model.Cliente;
import com.neisser.dev.bpa.model.TipoCliente;
import com.neisser.dev.bpa.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        if (clienteRepository.existsByDocumento(request.documento())) {
            throw new ResourceConflictException("Ya existe un cliente con ese documento");
        }

        return clienteMapper.toResponse(
                clienteRepository.save(clienteMapper.toEntity(request)));
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("cliente", id));

        return clienteMapper.toResponse(cliente);
    }

    @Transactional
    public ClienteResponse actualizar(Long id, ClienteUpdateRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("cliente", id));

        if (request.documento() != null
                && !request.documento().equals(cliente.getDocumento())
                && clienteRepository.existsByDocumento(request.documento())) {
            throw new ResourceConflictException("Ya existe un cliente con ese documento");
        }

        if (cliente.getTipoCliente() == TipoCliente.PARTICULAR
                && request.clasificacionEmpresa() != null) {
            throw new BusinessRuleViolationException(
                    "Un cliente particular no puede tener clasificación empresarial");
        }

        clienteMapper.actualizarEntidad(cliente, request);

        return clienteMapper.toResponse(cliente);
    }
}
