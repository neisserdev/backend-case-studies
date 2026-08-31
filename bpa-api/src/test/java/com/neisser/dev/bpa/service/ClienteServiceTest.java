package com.neisser.dev.bpa.service;

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

import com.neisser.dev.bpa.dto.ClienteDTOs.ClienteUpdateRequest;
import com.neisser.dev.bpa.exceptions.BusinessRuleViolationException;
import com.neisser.dev.bpa.exceptions.ResourceConflictException;
import com.neisser.dev.bpa.mapper.ClienteMapper;
import com.neisser.dev.bpa.model.ClasificacionEmpresa;
import com.neisser.dev.bpa.model.Cliente;
import com.neisser.dev.bpa.model.TipoCliente;
import com.neisser.dev.bpa.repository.ClienteRepository;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Spy
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void actualizacionParcialConservaLosCamposNoEnviados() {
        Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("Nombre anterior")
                .documento("DOC-1")
                .tipoCliente(TipoCliente.PARTICULAR)
                .build();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        clienteService.actualizar(1L, new ClienteUpdateRequest("Nombre nuevo", null, null));

        assertEquals("Nombre nuevo", cliente.getNombre());
        assertEquals("DOC-1", cliente.getDocumento());
    }

    @Test
    void actualizarDocumentoRechazaDuplicados() {
        Cliente cliente = Cliente.builder()
                .id(2L)
                .documento("DOC-2")
                .tipoCliente(TipoCliente.PARTICULAR)
                .build();
        when(clienteRepository.findById(2L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.existsByDocumento("DOC-1")).thenReturn(true);

        assertThrows(ResourceConflictException.class,
                () -> clienteService.actualizar(2L, new ClienteUpdateRequest(null, "DOC-1", null)));
    }

    @Test
    void particularNoPuedeRecibirClasificacionEmpresarial() {
        Cliente cliente = Cliente.builder()
                .id(1L)
                .documento("DOC-1")
                .tipoCliente(TipoCliente.PARTICULAR)
                .build();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        assertThrows(BusinessRuleViolationException.class,
                () -> clienteService.actualizar(1L,
                        new ClienteUpdateRequest(null, null, ClasificacionEmpresa.RENTABLE)));
    }
}
