package com.neisser.dev.bpa.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neisser.dev.bpa.model.Operacion;
import com.neisser.dev.bpa.model.TipoOperacion;

public interface OperacionRepository extends JpaRepository<Operacion, Long> {
    List<Operacion> findByCuentaIdOrderByFechaDesc(Long cuentaId);

    boolean existsByCuentaIdAndTipoOperacionAndFechaGreaterThanEqualAndFechaLessThan(
            Long cuentaId, TipoOperacion tipoOperacion, LocalDateTime inicio, LocalDateTime fin);
}
