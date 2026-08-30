package com.neisser.dev.bpa.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neisser.dev.bpa.dto.CuentaDTOs.CuentaRequest;
import com.neisser.dev.bpa.dto.CuentaDTOs.CuentaResponse;
import com.neisser.dev.bpa.dto.OperacionDTOs.OperacionRequest;
import com.neisser.dev.bpa.dto.OperacionDTOs.OperacionResponse;
import com.neisser.dev.bpa.exceptions.BusinessRuleViolationException;
import com.neisser.dev.bpa.exceptions.ResourceNotFoundException;
import com.neisser.dev.bpa.mapper.CuentaMapper;
import com.neisser.dev.bpa.mapper.OperacionMapper;
import com.neisser.dev.bpa.model.Cliente;
import com.neisser.dev.bpa.model.ClasificacionEmpresa;
import com.neisser.dev.bpa.model.Cuenta;
import com.neisser.dev.bpa.model.EstadoCuenta;
import com.neisser.dev.bpa.model.Operacion;
import com.neisser.dev.bpa.model.TipoOperacion;
import com.neisser.dev.bpa.repository.ClienteRepository;
import com.neisser.dev.bpa.repository.CuentaRepository;
import com.neisser.dev.bpa.repository.OperacionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CuentaService {

    private static final BigDecimal DEPOSITO_INICIAL_MINIMO = new BigDecimal("50");

    private static final BigDecimal TASA_PARTICULAR = new BigDecimal("0.025");
    private static final BigDecimal TASA_EMPRESA_RENTABLE = new BigDecimal("0.035");
    private static final BigDecimal TASA_EMPRESA_NO_RENTABLE = new BigDecimal("0.015");

    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;
    private final OperacionRepository operacionRepository;

    private final CuentaMapper cuentaMapper;
    private final OperacionMapper operacionMapper;

    @Transactional
    public CuentaResponse crear(CuentaRequest request) {

        if (request.depositoInicial().compareTo(DEPOSITO_INICIAL_MINIMO) < 0) {
            throw new BusinessRuleViolationException(
                    "El depósito inicial debe ser de al menos $50");
        }

        Cliente cliente = clienteRepository
                .findById(request.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException("cliente", request.clienteId()));

        Cuenta cuenta = cuentaMapper.toEntity(cliente, request.depositoInicial());

        return cuentaMapper.toResponse(cuentaRepository.save(cuenta));
    }

    @Transactional(readOnly = true)
    public CuentaResponse buscarPorId(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("cuenta", id));

        return cuentaMapper.toResponse(cuenta);
    }

    @Transactional
    public CuentaResponse cerrar(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("cuenta", id));

        if (cuenta.getEstadoCuenta() == EstadoCuenta.CERRADA) {
            throw new BusinessRuleViolationException("La cuenta ya se encuentra cerrada");
        }

        cuenta.setEstadoCuenta(EstadoCuenta.CERRADA);

        return cuentaMapper.toResponse(cuenta);
    }

    @Transactional
    public OperacionResponse depositar(Long cuentaId, OperacionRequest request) {

        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new ResourceNotFoundException("cuenta", cuentaId));

        if (cuenta.getEstadoCuenta() == EstadoCuenta.CERRADA) {
            throw new BusinessRuleViolationException("No se puede operar sobre una cuenta cerrada");
        }

        BigDecimal nuevoSaldo = cuenta.getSaldo().add(request.monto());
        cuenta.setSaldo(nuevoSaldo);

        if (cuenta.getEstadoCuenta() == EstadoCuenta.BLOQUEADA) {
            cuenta.setEstadoCuenta(EstadoCuenta.ACTIVA);
        }

        Operacion operacion = operacionMapper.toEntity(request, cuenta);
        operacion.setTipoOperacion(TipoOperacion.DEPOSITO);
        operacion.setSaldoResultante(nuevoSaldo);

        return operacionMapper.toResponse(operacionRepository.save(operacion));
    }

    @Transactional
    public OperacionResponse extraer(Long cuentaId, OperacionRequest request) {

        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new ResourceNotFoundException("cuenta", cuentaId));

        if (cuenta.getEstadoCuenta() == EstadoCuenta.CERRADA) {
            throw new BusinessRuleViolationException("No se puede operar sobre una cuenta cerrada");
        }

        if (cuenta.getEstadoCuenta() == EstadoCuenta.BLOQUEADA) {
            throw new BusinessRuleViolationException("Una cuenta bloqueada no admite extracciones");
        }

        BigDecimal nuevoSaldo = cuenta.getSaldo().subtract(request.monto());
        cuenta.setSaldo(nuevoSaldo);

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            cuenta.setEstadoCuenta(EstadoCuenta.BLOQUEADA);
        }

        Operacion operacion = operacionMapper.toEntity(request, cuenta);
        operacion.setTipoOperacion(TipoOperacion.EXTRACCION);
        operacion.setSaldoResultante(nuevoSaldo);

        return operacionMapper.toResponse(operacionRepository.save(operacion));
    }

    @Transactional(readOnly = true)
    public List<OperacionResponse> historial(Long cuentaId) {

        if (!cuentaRepository.existsById(cuentaId)) {
            throw new ResourceNotFoundException("cuenta", cuentaId);
        }

        return operacionRepository
                .findByCuentaIdOrderByFechaDesc(cuentaId)
                .stream()
                .map(operacionMapper::toResponse)
                .toList();
    }

    @Transactional
    public OperacionResponse aplicarInteres(Long cuentaId) {

        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new ResourceNotFoundException("cuenta", cuentaId));

        if (cuenta.getEstadoCuenta() == EstadoCuenta.CERRADA) {
            throw new BusinessRuleViolationException("No se puede aplicar interés a una cuenta cerrada");
        }

        Cliente cliente = cuentaRepository.buscarClientePorCuentaId(cuentaId)
                .orElseThrow(() -> new ResourceNotFoundException("cliente", cuentaId));

        BigDecimal tasa = tasaDe(cliente);
        BigDecimal interes = cuenta.getSaldo().multiply(tasa);
        BigDecimal nuevoSaldo = cuenta.getSaldo().add(interes);

        cuenta.setSaldo(nuevoSaldo);

        Operacion operacion = Operacion.builder()
                .tipoOperacion(TipoOperacion.INTERES)
                .monto(interes)
                .saldoResultante(nuevoSaldo)
                .cuenta(cuenta)
                .build();

        return operacionMapper.toResponse(operacionRepository.save(operacion));
    }

    @Transactional
    public List<OperacionResponse> aplicarInteresAnual() {

        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioDelAnio = hoy.withDayOfYear(1).atStartOfDay();
        LocalDateTime inicioDelProximoAnio = hoy.plusYears(1).withDayOfYear(1).atStartOfDay();

        List<Cuenta> cuentasActivas = cuentaRepository.findAll().stream()
                .filter(c -> c.getEstadoCuenta() == EstadoCuenta.ACTIVA)
                .filter(c -> !operacionRepository.existsByCuentaIdAndTipoOperacionAndFechaBetween(
                        c.getId(), TipoOperacion.INTERES, inicioDelAnio, inicioDelProximoAnio))
                .toList();

        return cuentasActivas.stream()
                .map(c -> aplicarInteres(c.getId()))
                .toList();
    }

    private BigDecimal tasaDe(Cliente cliente) {
        return switch (cliente.getTipoCliente()) {
            case PARTICULAR -> TASA_PARTICULAR;
            case EMPRESA -> cliente.getClasificacionEmpresa() == ClasificacionEmpresa.RENTABLE
                    ? TASA_EMPRESA_RENTABLE
                    : TASA_EMPRESA_NO_RENTABLE;
        };
    }
}
