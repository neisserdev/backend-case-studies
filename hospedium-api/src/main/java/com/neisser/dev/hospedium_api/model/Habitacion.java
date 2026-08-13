package com.neisser.dev.hospedium_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "habitacion")
public class Habitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "habitacion_id")
    private Long id;

    @Column(name = "numero_habitacion")
    private String numero;

    @Column(name = "piso")
    private Integer piso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_habitacion_id", nullable = false)
    private TipoHabitacion tipoHabitacion;
}
