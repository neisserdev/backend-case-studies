package com.neisser.dev.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neisser.dev.biblioteca.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByDocumento(String documento);
}
