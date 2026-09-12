package com.neisser.dev.biblioteca.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.neisser.dev.biblioteca.dto.UsuarioDTOs.UsuarioUpdateRequest;
import com.neisser.dev.biblioteca.exceptions.BusinessRuleViolationException;
import com.neisser.dev.biblioteca.exceptions.ResourceConflictException;
import com.neisser.dev.biblioteca.mapper.UsuarioMapper;
import com.neisser.dev.biblioteca.model.TipoUsuario;
import com.neisser.dev.biblioteca.model.Usuario;
import com.neisser.dev.biblioteca.repository.PrestamoRepository;
import com.neisser.dev.biblioteca.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PrestamoRepository prestamoRepository;

    @Spy
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void actualizacionParcialConservaLosCamposNoEnviados() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombre("Nombre anterior")
                .documento("DOC-1")
                .tipoUsuario(TipoUsuario.INSCRITO)
                .build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.actualizar(1L, new UsuarioUpdateRequest("Nombre nuevo", null));

        assertEquals("Nombre nuevo", usuario.getNombre());
        assertEquals("DOC-1", usuario.getDocumento());
    }

    @Test
    void actualizarDocumentoRechazaDuplicados() {
        Usuario usuario = Usuario.builder()
                .id(2L)
                .documento("DOC-2")
                .tipoUsuario(TipoUsuario.INSCRITO)
                .build();
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByDocumento("DOC-1")).thenReturn(true);

        assertThrows(ResourceConflictException.class,
                () -> usuarioService.actualizar(2L, new UsuarioUpdateRequest(null, "DOC-1")));
    }

    @Test
    void noSePuedeEliminarUnUsuarioConPrestamosAbiertos() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .documento("DOC-1")
                .tipoUsuario(TipoUsuario.INSCRITO)
                .build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(prestamoRepository.countByUsuarioIdAndFechaDevolucionRealIsNull(1L)).thenReturn(1L);

        assertThrows(BusinessRuleViolationException.class,
                () -> usuarioService.eliminar(1L));
    }
}
