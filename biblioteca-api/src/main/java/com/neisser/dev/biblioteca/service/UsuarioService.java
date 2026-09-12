package com.neisser.dev.biblioteca.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neisser.dev.biblioteca.dto.UsuarioDTOs.UsuarioRequest;
import com.neisser.dev.biblioteca.dto.UsuarioDTOs.UsuarioResponse;
import com.neisser.dev.biblioteca.dto.UsuarioDTOs.UsuarioUpdateRequest;
import com.neisser.dev.biblioteca.exceptions.BusinessRuleViolationException;
import com.neisser.dev.biblioteca.exceptions.ResourceConflictException;
import com.neisser.dev.biblioteca.exceptions.ResourceNotFoundException;
import com.neisser.dev.biblioteca.mapper.UsuarioMapper;
import com.neisser.dev.biblioteca.model.Usuario;
import com.neisser.dev.biblioteca.repository.PrestamoRepository;
import com.neisser.dev.biblioteca.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PrestamoRepository prestamoRepository;
    private final UsuarioMapper usuarioMapper;

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByDocumento(request.documento())) {
            throw new ResourceConflictException("Ya existe un usuario con ese documento");
        }

        return usuarioMapper.toResponse(
                usuarioRepository.save(usuarioMapper.toEntity(request)));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("usuario", id));

        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("usuario", id));

        if (request.documento() != null
                && !request.documento().equals(usuario.getDocumento())
                && usuarioRepository.existsByDocumento(request.documento())) {
            throw new ResourceConflictException("Ya existe un usuario con ese documento");
        }

        usuarioMapper.actualizarEntidad(usuario, request);

        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("usuario", id));

        if (prestamoRepository.countByUsuarioIdAndFechaDevolucionRealIsNull(usuario.getId()) > 0) {
            throw new BusinessRuleViolationException(
                    "No se puede eliminar un usuario con préstamos abiertos");
        }

        usuarioRepository.delete(usuario);
    }
}
