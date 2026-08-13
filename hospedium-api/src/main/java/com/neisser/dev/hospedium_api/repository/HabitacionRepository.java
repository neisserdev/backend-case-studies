package com.neisser.dev.hospedium_api.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.neisser.dev.hospedium_api.model.Habitacion;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {

    @Query("""
            SELECT h
            FROM Habitacion h
            WHERE h.tipoHabitacion.id = :tipoId
              AND NOT EXISTS (
                  SELECT a
                  FROM Asignacion a
                  WHERE a.habitacion = h
                    AND a.reserva.fechaEntrada < :fechaSalida
                    AND a.reserva.fechaSalida > :fechaEntrada
              )
            """)
    List<Habitacion> buscarDisponibles(
            Long tipoId,
            LocalDate fechaEntrada,
            LocalDate fechaSalida);

    boolean existsByTipoHabitacionId(Long tipoHabitacionId);

}
