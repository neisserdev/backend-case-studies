package com.neisser.dev.biblioteca.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.neisser.dev.biblioteca.model.Ejemplar;

import jakarta.persistence.LockModeType;

public interface EjemplarRepository extends JpaRepository<Ejemplar, Long> {
    List<Ejemplar> findByLibroId(Long libroId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Ejemplar e WHERE e.id = :id")
    Optional<Ejemplar> findByIdForUpdate(Long id);
}
