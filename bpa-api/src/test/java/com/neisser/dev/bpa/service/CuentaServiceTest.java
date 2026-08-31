package com.neisser.dev.bpa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.neisser.dev.bpa.dto.CuentaDTOs.CuentaRequest;
import com.neisser.dev.bpa.dto.OperacionDTOs.OperacionRequest;
import com.neisser.dev.bpa.mapper.CuentaMapper;
import com.neisser.dev.bpa.mapper.OperacionMapper;
import com.neisser.dev.bpa.model.Cliente;
import com.neisser.dev.bpa.model.Cuenta;
import com.neisser.dev.bpa.model.EstadoCuenta;
import com.neisser.dev.bpa.model.Operacion;
import com.neisser.dev.bpa.model.TipoCliente;
import com.neisser.dev.bpa.model.TipoOperacion;
import com.neisser.dev.bpa.repository.ClienteRepository;
import com.neisser.dev.bpa.repository.CuentaRepository;
import com.neisser.dev.bpa.repository.OperacionRepository;

@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private OperacionRepository operacionRepository;

    @Spy
    private CuentaMapper cuentaMapper;

    @Spy
    private OperacionMapper operacionMapper;

    @InjectMocks
    private CuentaService cuentaService;

    @Test
    void crearCuentaRegistraElDepositoInicialEnElHistorial() {
        Cliente cliente = Cliente.builder().id(1L).tipoCliente(TipoCliente.PARTICULAR).build();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(invocation -> {
            Cuenta cuenta = invocation.getArgument(0);
            cuenta.setId(10L);
            return cuenta;
        });
        when(operacionRepository.save(any(Operacion.class))).thenAnswer(invocation -> {
            Operacion operacion = invocation.getArgument(0);
            operacion.setId(20L);
            return operacion;
        });

        cuentaService.crear(new CuentaRequest(1L, new BigDecimal("50.00")));

        ArgumentCaptor<Operacion> captor = ArgumentCaptor.forClass(Operacion.class);
        verify(operacionRepository).save(captor.capture());
        Operacion inicial = captor.getValue();
        assertEquals(TipoOperacion.DEPOSITO, inicial.getTipoOperacion());
        assertEquals(new BigDecimal("50.00"), inicial.getMonto());
        assertEquals(new BigDecimal("50.00"), inicial.getSaldoResultante());
        assertEquals(10L, inicial.getCuenta().getId());
    }

    @Test
    void cualquierDepositoReactivaUnaCuentaBloqueadaAunqueElSaldoSigaNegativo() {
        Cuenta cuenta = Cuenta.builder()
                .id(1L)
                .saldo(new BigDecimal("-10.00"))
                .estadoCuenta(EstadoCuenta.BLOQUEADA)
                .build();
        when(cuentaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(cuenta));
        when(operacionRepository.save(any(Operacion.class))).thenAnswer(invocation -> {
            Operacion operacion = invocation.getArgument(0);
            operacion.setId(1L);
            return operacion;
        });

        cuentaService.depositar(1L, new OperacionRequest(new BigDecimal("1.00")));

        assertEquals(EstadoCuenta.ACTIVA, cuenta.getEstadoCuenta());
        assertEquals(new BigDecimal("-9.00"), cuenta.getSaldo());
    }

    @Test
    void interesAnualNoSeAplicaDosVecesEnElMismoAnio() {
        Cliente cliente = Cliente.builder().id(1L).tipoCliente(TipoCliente.PARTICULAR).build();
        Cuenta cuenta = Cuenta.builder()
                .id(1L)
                .cliente(cliente)
                .saldo(new BigDecimal("100.00"))
                .estadoCuenta(EstadoCuenta.ACTIVA)
                .build();
        when(cuentaRepository.findByEstadoCuentaForUpdate(EstadoCuenta.ACTIVA))
                .thenReturn(List.of(cuenta));
        when(operacionRepository.existsByCuentaIdAndTipoOperacionAndFechaGreaterThanEqualAndFechaLessThan(
                any(), any(), any(), any()))
                .thenReturn(false, true);
        when(cuentaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.buscarClientePorCuentaId(1L)).thenReturn(Optional.of(cliente));
        when(operacionRepository.save(any(Operacion.class))).thenAnswer(invocation -> {
            Operacion operacion = invocation.getArgument(0);
            operacion.setId(1L);
            return operacion;
        });

        assertEquals(1, cuentaService.aplicarInteresAnual().size());
        assertEquals(new BigDecimal("102.50"), cuenta.getSaldo());
        assertTrue(cuentaService.aplicarInteresAnual().isEmpty());
        assertEquals(new BigDecimal("102.50"), cuenta.getSaldo());
    }
}
