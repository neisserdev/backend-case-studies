package com.neisser.dev.hospedium_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neisser.dev.hospedium_api.model.Huesped;

public interface HuespedRepository extends JpaRepository<Huesped, Long> {
    boolean existsByPasaporte(String pasaporte);
}
