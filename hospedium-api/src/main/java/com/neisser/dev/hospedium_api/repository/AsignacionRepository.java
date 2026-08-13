package com.neisser.dev.hospedium_api.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.neisser.dev.hospedium_api.model.Asignacion;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    @Query("""
                SELECT COUNT(a) > 0
                FROM Asignacion a
                WHERE a.habitacion.id = :habitacionId
                  AND a.reserva.fechaEntrada < :fechaSalida
                  AND a.reserva.fechaSalida > :fechaEntrada
            """)
    boolean existeChoque(
            Long habitacionId,
            LocalDate fechaEntrada,
            LocalDate fechaSalida);

    boolean existsByHabitacionId(Long habitacionId);

}
