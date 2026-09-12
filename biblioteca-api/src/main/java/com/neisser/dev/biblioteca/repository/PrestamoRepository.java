package com.neisser.dev.biblioteca.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neisser.dev.biblioteca.model.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    boolean existsByEjemplarIdAndFechaDevolucionRealIsNull(Long ejemplarId);

    long countByUsuarioIdAndFechaDevolucionRealIsNull(Long usuarioId);

    @Query("""
                SELECT COUNT(p)
                FROM Prestamo p
                WHERE p.ejemplar.libro.id = :libroId
                AND p.fechaPrestamo >= :inicioMes
                AND p.fechaPrestamo < :finMes
            """)
    long contarPrestamosDelLibroEnElMes(
            @Param("libroId") Long libroId,
            @Param("inicioMes") LocalDateTime inicioMes,
            @Param("finMes") LocalDateTime finMes);

    List<Prestamo> findByUsuarioIdOrderByFechaPrestamoDesc(Long usuarioId);
}
