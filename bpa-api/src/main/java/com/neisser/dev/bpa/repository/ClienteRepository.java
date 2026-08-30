package com.neisser.dev.bpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neisser.dev.bpa.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByDocumento(String documento);
}
