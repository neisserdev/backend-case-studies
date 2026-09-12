package com.neisser.dev.biblioteca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.neisser.dev.biblioteca.model.Genero;
import com.neisser.dev.biblioteca.model.Libro;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    boolean existsByCodigo(String codigo);

    @Query("""
                SELECT DISTINCT e.libro
                FROM Ejemplar e
                WHERE e.libro.genero = :genero
                AND e.estadoEjemplar = com.neisser.dev.biblioteca.model.EstadoEjemplar.DISPONIBLE
            """)
    List<Libro> buscarConEjemplarDisponiblePorGenero(Genero genero);
}
