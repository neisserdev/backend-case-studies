package com.neisser.dev.hospedium_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neisser.dev.hospedium_api.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByHuespedIdOrderByFechaEntradaDesc(Long huespedId);
}
