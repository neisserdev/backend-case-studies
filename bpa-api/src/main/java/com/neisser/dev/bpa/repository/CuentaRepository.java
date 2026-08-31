package com.neisser.dev.bpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.neisser.dev.bpa.model.Cliente;
import com.neisser.dev.bpa.model.Cuenta;
import com.neisser.dev.bpa.model.EstadoCuenta;

import jakarta.persistence.LockModeType;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cuenta c WHERE c.id = :id")
    Optional<Cuenta> findByIdForUpdate(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cuenta c WHERE c.estadoCuenta = :estadoCuenta")
    List<Cuenta> findByEstadoCuentaForUpdate(EstadoCuenta estadoCuenta);

    @Query("""
                SELECT c.cliente
                FROM Cuenta c
                WHERE c.id = :cuentaId
            """)
    Optional<Cliente> buscarClientePorCuentaId(Long cuentaId);
}
