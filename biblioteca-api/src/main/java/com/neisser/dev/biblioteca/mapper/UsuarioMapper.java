package com.neisser.dev.biblioteca.mapper;

import org.springframework.stereotype.Component;

import com.neisser.dev.biblioteca.dto.UsuarioDTOs.UsuarioRequest;
import com.neisser.dev.biblioteca.dto.UsuarioDTOs.UsuarioResponse;
import com.neisser.dev.biblioteca.dto.UsuarioDTOs.UsuarioUpdateRequest;
import com.neisser.dev.biblioteca.model.Usuario;

@Component
public class UsuarioMapper {
    public Usuario toEntity(UsuarioRequest request) {
        return Usuario.builder()
                .nombre(request.nombre())
                .documento(request.documento())
                .tipoUsuario(request.tipoUsuario())
                .build();
    }

    public void actualizarEntidad(Usuario usuario, UsuarioUpdateRequest request) {
        if (request.nombre() != null) {
            usuario.setNombre(request.nombre());
        }
        if (request.documento() != null) {
            usuario.setDocumento(request.documento());
        }
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getDocumento(),
                usuario.getTipoUsuario());
    }
}
