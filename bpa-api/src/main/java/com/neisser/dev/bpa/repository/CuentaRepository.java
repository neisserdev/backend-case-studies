package com.neisser.dev.bpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.neisser.dev.bpa.model.Cliente;
import com.neisser.dev.bpa.model.Cuenta;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    @Query("""
                SELECT c.cliente
                FROM Cuenta c
                WHERE c.id = :cuentaId
            """)
    Optional<Cliente> buscarClientePorCuentaId(Long cuentaId);
}
